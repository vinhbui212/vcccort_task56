package thuctap.task4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thuctap.task4.exception.UserException;
import thuctap.task4.model.User;
import thuctap.task4.repo.UserDAOImpl;
import thuctap.task4.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserDAOImpl userDAO;

    @Autowired
    public UserController(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        try {

            userDAO.insertUser(user);

            return new ResponseEntity<>(ApiResponse.success("User created successfully", user), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal server error", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable int id, @RequestBody User user) {
        try {
            userDAO.updateUser(id, user);
            User user1=userDAO.searchById(id);
            return new ResponseEntity<>(ApiResponse.success("User updated successfully", user1), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal server error", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable int id) {
        try {
            userDAO.deleteUser(id);
            return new ResponseEntity<>(ApiResponse.success("User deleted successfully","deleted"+id), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal server error", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable int id) {
        try {
            User user = userDAO.searchById(id);
            if (user != null) {
                return new ResponseEntity<>(ApiResponse.success("User information", user), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ApiResponse.error("User not found", 404), HttpStatus.NOT_FOUND);
            }
        } catch (UserException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal server error", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchUsers(@RequestParam(required = false) String name,
                                                      @RequestParam(required = false) Integer limit,
                                                      @RequestParam(required = false) Integer offset,
                                                      @RequestParam(required = false) String address) {
        try {
            List<User> users;
            if (name != null) {
                users = userDAO.searchByName(name, limit != null ? limit : 10, offset != null ? offset : 0);
            } else if (address != null) {
                users = userDAO.searchByAddress(address);
            } else {
                return new ResponseEntity<>(ApiResponse.error("Please provide a search parameter", 900), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(ApiResponse.success("Search results", users), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal server error", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        try {
            List<User> users = userDAO.arrangeByName();
            return new ResponseEntity<>(ApiResponse.success("All users", users), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getCode()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error("Internal server error", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
