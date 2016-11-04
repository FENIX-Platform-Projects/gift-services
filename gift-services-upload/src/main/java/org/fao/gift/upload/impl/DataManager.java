package org.fao.gift.upload.impl;

import org.fao.gift.upload.dto.Queries;
import org.fao.gift.utils.DataSource;
import org.fao.fenix.commons.utils.FileUtils;
import org.fao.fenix.commons.utils.UIDUtils;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;

import javax.inject.Inject;
import javax.ws.rs.NotAcceptableException;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class DataManager {
    private static char CSV_SEPARATOR = ',';

    @Inject private DataSource dataSource;
    @Inject private DatabaseUtils databaseUtils;
    @Inject private FileUtils fileUtils;
    @Inject private UIDUtils uidUtils;


    public Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }

    public void uploadCSV(File csvData, String tmpTableName, Connection connection) throws Exception {
        StringBuilder query = new StringBuilder("COPY ").append(tmpTableName).append(" FROM STDIN WITH CSV HEADER DELIMITER '").append(CSV_SEPARATOR).append("' QUOTE '\"' ENCODING 'UTF8'");
        CopyIn copier = ((PGConnection)connection).getCopyAPI().copyIn(query.toString());
        byte[] data = fileUtils.readTextFile(csvData).getBytes();
        copier.writeToCopy(data,0,data.length);
        copier.flushCopy();
        copier.endCopy();
    }

    public void validateSurveyData(Connection connection) throws Exception {
        //Verify data food codes are assigned to a group
        StringBuilder error = new StringBuilder();
        for (ResultSet resultSet = connection.createStatement().executeQuery(Queries.getUnexistingFoodGroup.getQuery()); resultSet.next(); error.append('\n').append(resultSet.getString(1)));
        if (error.length()>0)
            throw new NotAcceptableException("Data have food codes with no group assigned: "+error.toString());
    }

    public void cleanTmpData(Connection connection) throws Exception {
        connection.createStatement().executeUpdate(Queries.cleanSubjectRaw.getQuery());
        connection.createStatement().executeUpdate(Queries.cleanConsumptionRaw.getQuery());
        connection.createStatement().executeUpdate(Queries.cleanFoodGroups.getQuery());
    }

    public void publishData(Connection connection, String surveyCode, Integer year) throws Exception {
        //Update survey data with raw tables content
        CallableStatement callStatement = connection.prepareCall(Queries.updateSubject.getQuery());
        callStatement.setString(1, surveyCode);
        callStatement.execute();
        callStatement = connection.prepareCall(Queries.updateConsumption.getQuery());
        callStatement.setString(1, surveyCode);
        callStatement.execute();
        callStatement = connection.prepareCall(Queries.updateSurveyIndex.getQuery());
        callStatement.setString(1, surveyCode);
        callStatement.setInt(2, year);
        callStatement.execute();
    }



}



/*
    //Utils
    SimpleDateFormat timeSuffixFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private String getTimeSuffix(Date... date) {
        return timeSuffixFormat.format(date!=null && date.length>0 ? date[0] : new Date());
    }
*/

//verify only one survey exists
/*        Collection<String> surveys = new LinkedList<>();
        for (ResultSet resultSet = connection.createStatement().executeQuery(Queries.getSurveyList.getQuery()); resultSet.next(); surveys.add(resultSet.getString(1)));
        if (surveys.size()!=1) {
            StringBuilder error = new StringBuilder("Uploaded data contains multiple surveys:");
            for (String surveyCode : surveys)
                error.append('\n').append(surveyCode);
            throw new UnsupportedOperationException(error.toString());
        }*/
//Return survey code
//        return surveys.iterator().next();
