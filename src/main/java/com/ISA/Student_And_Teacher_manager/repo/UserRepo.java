package com.ISA.Student_And_Teacher_manager.repo;

import com.ISA.Student_And_Teacher_manager.users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User,Integer> {
    @Query("SELECT u FROM User u WHERE u.name = ?1")
    public User findUserByUserName(String username);
}
