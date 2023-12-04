package com.parag.lily.database;

import com.parag.lily.Utility;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class DBSource {
    // Todo: move all secrets to vault
    private static final String POSTGRES_DB = "root";
    private static final String POSTGRES_USER = "root";
    private static final String POSTGRES_PASSWORD = "atlan";

    private static final String CRD_DB = "defaultdb";
    private static final String CRD_USER = "root";
    private static final String CRD_PASSWORD = "root";

    private static final Logger LOGGER = Utility.getLogger();

    public static DataSource getPostgres(){
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setDatabaseName(POSTGRES_DB);
        source.setUser(POSTGRES_USER);
        source.setPassword(POSTGRES_PASSWORD);
        return source;
    }

    public static DataSource getCockroachDb(){
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setDatabaseName(CRD_DB);
        source.setPortNumbers(new int[]{26257});
        source.setSsl(false);
        source.setUser(CRD_USER);
        return source;
    }

    public static void initDataBases(){
        runSqlScript(getPostgres(), "/database/postgres/reset_schema.sql");
        runSqlScript(getCockroachDb(), "/database/crd/reset_schema.sql");
        runSqlScript(getCockroachDb(), "/database/crd/reset_data.sql");
    }
    public static void runSqlScript(DataSource source, String sqlPath){
        try (Connection con = source.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(con);
            scriptRunner.runScript(sqlReader(sqlPath));
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException(String.format("Failed while running sql %s, Error: %s", sqlPath, e));
        }
    }

    private static Reader sqlReader(String filePath) {
        InputStream in = DBSource.class.getResourceAsStream(filePath);
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)));
    }
}
