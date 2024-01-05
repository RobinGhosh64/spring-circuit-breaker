Hello, in this article, we will create an Exception Handling mechanism for use in a Spring Boot project. Managing errors in a project is one of the most important aspects since it is crucial for the server to work continuously and correctly. Handling errors and exceptions that may occur in the program flow in an appropriate manner is necessary. Otherwise, it can affect the user’s experience.

As the project grows, the number of possible exceptions that can occur will increase. Therefore, to reduce this problem and simplify error management as much as possible, we need to build certain structures. In this article, we will create a GlobalExceptionHandler mechanism to centralize the handling of exceptions that may occur during a request and return an appropriate response.

When an exception occurs, the Spring Boot application will redirect it to the GlobalExceptionHandler. Depending on the specific type of exception, the GlobalExceptionHandler can generate an appropriate error message and return it to the client. This helps provide more meaningful error messages to the clients. It can also be used to log detailed information about exceptions.

We will continue explaining the topic using a project we have previously written. We will integrate this mechanism into the mentioned project. Below you can see the StudentController class:




Controller:


````shell

package com.ffx.springboot_mongodb.controller;

import com.ffx.springboot_mongodb.dto.StudentDto;
import com.ffx.springboot_mongodb.mapper.IStudentMapper;
import com.ffx.springboot_mongodb.service.interfaces.IStudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class StudentController {
    private final IStudentService studentService;
    private final IStudentMapper studentMapper;
    @GetMapping("/student/all")
    public List<StudentDto> findAllStudents() {
        return studentService.findAllStudents().stream()
                .map(studentMapper::fromStudent).collect(Collectors.toList());
    }
    @GetMapping("/student/id/{id}")
    public ResponseEntity<StudentDto> findStudentById(@PathVariable String id) {
        var student = studentService.findStudentById(id);
        if(student == null) {
            return ResponseEntity.notFound().build();
        }
        var foundStudent = studentMapper.fromStudent(student);
        return ResponseEntity.ok(foundStudent);
    }
    @PostMapping("/student")
    public ResponseEntity<StudentDto> insertStudent(@RequestBody StudentDto studentDto) {
        var studentToInsert = studentMapper.toStudent(studentDto);
        var insertedStudent = studentService.insertStudent(studentToInsert);
        var insertedStudentDto = studentMapper.fromStudent(insertedStudent);
        return ResponseEntity.ok(insertedStudentDto);
    }
}

````


Lets see StudentService class:





````shell

package com.ffx.springboot_mongodb.service.impl;

import com.ffx.springboot_mongodb.document.Student;
import com.ffx.springboot_mongodb.repository.IStudentRepository;
import com.ffx.springboot_mongodb.service.interfaces.IStudentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService implements IStudentService {
    private final IStudentRepository studentRepository;
    public StudentService(IStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    @Override
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }
    @Override
    public Student findStudentById(String id) {
        return studentRepository.findById(id).orElse(null);
    }
    @Override
    public Student insertStudent(Student student) {
        return studentRepository.insert(student);
    }
}

````



As we can see from the above classes, there are some mechanisms missing to catch and handle possible errors. Let’s create our own Exception classes below and use them in the above classes to manage potential errors:

**StudentNotFoundException**
**StudentAlreadyExistsException**
Keeping it as simple as possible, let’s derive the previous classes from the RunTimeException class:




Our own classes:
StudentNotFoundException

````shell

package com.ffx.springboot_mongodb.exception;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(String message){
        super(message);
    }
}

````
StudentAlreadyExistsException
````shell

package com.ffx.springboot_mongodb.exception;

public class StudentAlreadyExistsException extends RuntimeException{
    public StudentAlreadyExistsException(String message){
        super(message);
    }
}

````

Let’s update the findStudentById method in the Service class as follows:


````shell



@Override
public Student findStudentById(String id) {
    return studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("Student not found with the given ID."));
}


````

With this update, we have made it possible to throw a relevant exception instead of returning null if no student is found in the searched id.


Now let’s update the insertStudent method as follows:

````shell



@Override
public Student insertStudent(Student student) {
    var existingStudent = studentRepository.findById(student.getId()).orElse(null);
    if(existingStudent != null){
        throw new StudentAlreadyExistsException("Student already exists with the given ID.");
    }
    return studentRepository.insert(student);
}


````

With this update, we first check whether a record has been made in the database with the ID of the student to be registered. If there is already a student with this id, we throw StudentAlreadyExistsException. If not, the database is registered.

After these changes, we need to make some changes in the StudentController class.

````shell



@GetMapping("/student/id/{id}")
public ResponseEntity<StudentDto> findStudentById(@PathVariable String id) {
    var student = studentService.findStudentById(id);
    var foundStudent = studentMapper.fromStudent(student);
    return ResponseEntity.ok(foundStudent);
}


````

Above, we no longer need to check if the variable student is null. Because if it is null, an exception will be thrown in the service layer anyway. In this way, we have also reduced the complexity and code clutter in the controller class.

Yep, we’re done here. So where do these thrown exceptions go now? If we run our service in this way, the thrown errors will disrupt the flow of the program and return undesirable results to the client.



How do we solve this problem?

GlobalExceptionHandler

To solve this problem, we will create a class and with @ControllerAdvice annotation we will make it listen for exceptions thrown from all controller classes in our spring project.


````shell

package com.ffx.springboot_mongodb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({StudentNotFoundException.class})
    public ResponseEntity<Object> handleStudentNotFoundException(StudentNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
    @ExceptionHandler({StudentAlreadyExistsException.class})
    public ResponseEntity<Object> handleStudentAlreadyExistsException(StudentAlreadyExistsException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}

````


The above structure will provide this process with basic principles. In a nutshell, this is where exceptions of the specified types are thrown, where they will be caught, and error messages specified by the developer will be delivered to the client.

Now let’s perform a test and query a student who is not in the database with his/her ID:


Response:


In this article, I tried to explain the GlobalExceptionHandler mechanism within the framework of basic principles. Integrating this structure into your projects at the initial stage will facilitate your work in the future and simplify exception-error management. It will also prevent unnecessary codes and ambiguity in Controller and Service layers.


