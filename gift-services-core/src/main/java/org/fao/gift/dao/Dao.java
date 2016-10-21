package org.fao.gift.dao;

import org.fao.gift.utils.DataSource;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class Dao {

    //@Resource(lookup = "java:jboss/postgresqllocal")
    //private DataSource datasource;
    @Inject DataSource dataSource;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    //Utils
    protected String buildStandardFilter (String[] names, Object[] values, Collection<Object> params) throws BadRequestException {
        if (names==null || values==null || params==null || names.length!=values.length)
            throw new BadRequestException();

        StringBuilder where = new StringBuilder();
        for (int i=0; i<names.length; i++)
            if (values[i]!=null) {
                where.append(" AND ").append(names[i]).append(" = ?");
                params.add(values[i]);
            }
        return where.length()>0 ? where.replace(0,4," WHERE").toString() : "";
    }



}
