package org.example.chapter8;

import com.novarto.sanedbc.core.interpreter.SyncDbInterpreter;
import com.novarto.sanedbc.core.interpreter.ValidationDbInterpreter;
import com.novarto.sanedbc.core.ops.EffectOp;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;

import static org.junit.Assert.*;

public class UserDAOTest
{
    private final static SyncDbInterpreter DB_INTERPRETER = new SyncDbInterpreter(
            () -> DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "")
    );

    private final static ValidationDbInterpreter VALIDATION_INTERPRETER = new ValidationDbInterpreter(
            () -> DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "")
    );


    @Before
    public void setUp()
    {
        DB_INTERPRETER.submit(UserDAO.createUsersDB());
    }

    @After
    public void cleanUp()
    {
        DB_INTERPRETER.submit(new EffectOp("DROP TABLE USERS"));
    }

    @Test
    public void insertUserGivenAccurateDetails()
    {
        Validation<Exception, Integer> userID = VALIDATION_INTERPRETER.submit(UserDAO.insertUser("name", "email", "1234"));
        assertTrue(userID.isSuccess());

        User user = DB_INTERPRETER.submit(UserDAO.selectUserByID(userID.success())).some();
        assertEquals("email", user.email);
        assertEquals("name", user.name);

        Validation<Exception, Integer> res = VALIDATION_INTERPRETER.submit(UserDAO.insertUser("otherName", "email", "1234"));
        assertTrue(res.isFail());
    }

    @Test
    public void insertUserGivenInaccurateDetails()
    {
        Validation<Exception, Integer> userID = VALIDATION_INTERPRETER.submit(UserDAO.insertUser(null, "email", "1234"));
        assertTrue(userID.isFail());

        userID = VALIDATION_INTERPRETER.submit(UserDAO.insertUser("name", null, "1234"));
        assertTrue(userID.isFail());

        userID = VALIDATION_INTERPRETER.submit(UserDAO.insertUser("name", "email", null));
        assertTrue(userID.isFail());
    }

    @Test
    public void updateUserName()
    {
        Integer userID = DB_INTERPRETER.submit(UserDAO.insertUser("name", "email", "1234"));

        Validation<Exception, Integer> rowsAffected = VALIDATION_INTERPRETER.submit(UserDAO.updateUserName("email", "newName"));
        assertTrue(rowsAffected.isSuccess());
        assertEquals(1, rowsAffected.success().intValue());

        User user = DB_INTERPRETER.submit(UserDAO.selectUserByID(userID)).some();
        assertEquals("newName", user.name);
    }

    @Test
    public void selectUserByID()
    {
        Integer userID = DB_INTERPRETER.submit(UserDAO.insertUser("name", "email", "1234"));

        assertTrue(DB_INTERPRETER.submit(UserDAO.selectUserByID(userID)).isSome());
        assertTrue(DB_INTERPRETER.submit(UserDAO.selectUserByID(-42)).isNone());
    }

    @Test
    public void selectUserByEmail()
    {
        DB_INTERPRETER.submit(UserDAO.insertUser("name", "email", "1234"));

        assertTrue(DB_INTERPRETER.submit(UserDAO.selectUserByEmail("email")).isSome());
        assertTrue(DB_INTERPRETER.submit(UserDAO.selectUserByEmail("other_email")).isNone());
    }

    @Test
    public void validate()
    {
        DB_INTERPRETER.submit(UserDAO.insertUser("name", "email", "1234"));

        assertTrue(DB_INTERPRETER.submit(UserDAO.validate("email", "1234")));
        assertFalse(DB_INTERPRETER.submit(UserDAO.validate("email", "wrongpass")));
    }
}