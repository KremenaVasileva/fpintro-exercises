package org.example.chapter8;

import com.novarto.sanedbc.core.ops.EffectOp;
import com.novarto.sanedbc.core.ops.InsertGenKeysOp;
import com.novarto.sanedbc.core.ops.SelectOp;
import com.novarto.sanedbc.core.ops.UpdateOp;
import fj.Unit;
import fj.control.db.DB;
import fj.data.Option;

import static com.novarto.sanedbc.core.ops.DbOps.unique;

public class UserDAO
{
    private final static int SALT_BYTES = 24;
    private final static int HASH_BYTES = 24;
    private final static int STRETCH_ITERATIONS = 1000;

    private final static PasswordHasher HASHER = new PasswordHasher(SALT_BYTES, HASH_BYTES, STRETCH_ITERATIONS);

    public static DB<Unit> createUsersDB()
    {
        return new EffectOp(
                "CREATE TABLE USERS " +
                        "(ID INTEGER PRIMARY KEY IDENTITY," +
                        " NAME NVARCHAR(100) NOT NULL," +
                        " EMAIL NVARCHAR(100) NOT NULL UNIQUE," +
                        " PASSWORD NVARCHAR(300) NOT NULL)"
        );
    }

    public static DB<Integer> insertUser(String name, String email, String password)
    {
        return new InsertGenKeysOp.Int(
                "INSERT INTO USERS (NAME, EMAIL, PASSWORD) VALUES (?, ?, ?)",
                ps ->
                {
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, HASHER.toHash(password));
                }
        );
    }

    public static DB<Integer> updateUserName(String email, String newName)
    {
        return new UpdateOp(
                "UPDATE USERS SET NAME = ? WHERE EMAIL = ?",
                ps ->
                {
                    ps.setString(1, newName);
                    ps.setString(2, email);
                }
        );
    }

    public static DB<Option<User>> selectUserByID(int id)
    {
        SelectOp.FjList<User> userOption = new SelectOp.FjList<>(
                "SELECT NAME, EMAIL FROM USERS WHERE ID = ?",
                ps -> ps.setInt(1, id),
                rs -> new User(id, rs.getString(1), rs.getString(2))
        );

        return unique(userOption);
    }

    public static DB<Option<User>> selectUserByEmail(String email)
    {
        SelectOp.FjList<User> userOption = new SelectOp.FjList<>(
                "SELECT ID, NAME FROM USERS WHERE EMAIL = ?",
                ps -> ps.setString(1, email),
                rs -> new User(rs.getInt(1), rs.getString(2), email)
        );

        return unique(userOption);
    }

    public static DB<Boolean> validate(String email, String pass)
    {
        SelectOp.FjList<String> dbPass = new SelectOp.FjList<>(
                "SELECT PASSWORD FROM USERS WHERE EMAIL = ?",
                ps -> ps.setString(1, email),
                rs -> rs.getString(1)
        );

        return unique(dbPass).map(passOp -> passOp.isSome() && HASHER.validatePassword(pass, passOp.some()));
    }
}
