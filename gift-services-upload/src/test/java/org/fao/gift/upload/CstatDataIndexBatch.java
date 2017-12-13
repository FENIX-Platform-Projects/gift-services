package org.fao.gift.upload;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.utils.D3SClient;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class CstatDataIndexBatch {


    //private static String baseUrl = "http://localhost:7777/v2/";
    private static String baseUrl = "http://fenixservices.fao.org/d3s/";
    //private static String orientUrl = "remote:localhost:2425/msd_2.0";
    private static String orientUrl = "remote:faostat3.fao.org:2427/msd_2.0";


    private D3SClient d3sClient;

    @Before
    public void setUp() throws Exception {
        d3sClient = CDISupport.getInstance(D3SClient.class);
    }


    @Test
    public void testAfg() throws Exception {
        indexContext("cstat_afg");
    }
    @Test
    public void testAgo() throws Exception {
        indexContext("cstat_ago");
    }
    @Test
    public void testBen() throws Exception {
        indexContext("cstat_ben");
    }
    @Test
    public void testBfa() throws Exception {
        indexContext("cstat_bfa");
    }
    @Test
    public void testCmr() throws Exception {
        indexContext("cstat_cmr");
    }
    @Test
    public void testCiv() throws Exception {
        indexContext("cstat_civ");
    }
    @Test
    public void testCog() throws Exception {
        indexContext("cstat_cog");
    }
    @Test
    public void testEth() throws Exception {
        indexContext("cstat_eth");
    }
    @Test
    public void testGha() throws Exception {
        indexContext("cstat_gha");
    }
    @Test
    public void testGab() throws Exception {
        indexContext("cstat_gab");
    }
    @Test
    public void testGnb() throws Exception {
        indexContext("cstat_gnb");
    }
    @Test
    public void testHti() throws Exception {
        indexContext("cstat_hti");
    }
    @Test
    public void testKen() throws Exception {
        indexContext("cstat_ken");
    }
    @Test
    public void testMdg() throws Exception {
        indexContext("cstat_mdg");
    }
    @Test
    public void testMwi() throws Exception {
        indexContext("cstat_mwi");
    }
    @Test
    public void testMli() throws Exception {
        indexContext("cstat_mli");
    }
    @Test
    public void testMoz() throws Exception {
        indexContext("cstat_moz");
    }
    @Test
    public void testMer() throws Exception {
        indexContext("cstat_ner");
    }
    @Test
    public void testNga() throws Exception {
        indexContext("cstat_nga");
    }
    @Test
    public void testRwa() throws Exception {
        indexContext("cstat_rwa");
    }
    @Test
    public void testSen() throws Exception {
        indexContext("cstat_sen");
    }
    @Test
    public void testTza() throws Exception {
        indexContext("cstat_tza");
    }
    @Test
    public void testTgo() throws Exception {
        indexContext("cstat_tgo");
    }
    @Test
    public void testUga() throws Exception {
        indexContext("cstat_uga");
    }
    @Test
    public void testZmb() throws Exception {
        indexContext("cstat_zmb");
    }
    @Test
    public void testTraining() throws Exception {
        indexContext("cstat_training");
    }



    //LOGIC
    private void indexContext(String context) throws Exception {
        ODatabaseDocument connection = new ODatabaseDocumentTx(orientUrl).open("admin","admin");
        try {
            for (MeIdentification<DSDDataset> datasetMetadata : d3sClient.retrieveMetadata(baseUrl, context)) {
                try {
                    Resource<DSDDataset, Object[]> dataset = d3sClient.getCstatDataset(baseUrl, datasetMetadata.getUid(), datasetMetadata.getVersion());
                    ODocument indexDocument = getIndexDocument(getId(datasetMetadata),connection);

                    indexDocument.field("index|id", getId(datasetMetadata));
                    indexDocument.field("index|freetext", getFreeTextValue(dataset));
                    indexDocument.field("index|dsd|contextSystem", dataset.getMetadata().getDsd().getContextSystem());

                    connection.save(indexDocument);
                } catch (Exception ex) {
                    System.out.println("Error on dataset "+datasetMetadata.getUid() + " - " + datasetMetadata.getVersion() + ":\n" + ex.getMessage());
                }
            }
        } finally {
            connection.close();
        }

    }


    private String getFreeTextValue (Resource<DSDDataset, Object[]> resource) throws Exception {
        StringBuilder text = new StringBuilder();
        //Find inclusions
        boolean[] inclusionMask = new boolean[resource.getMetadata().getDsd().getColumns().size()];
        Iterator<DSDColumn> columnIterator = resource.getMetadata().getDsd().getColumns().iterator();
        for (int i=0; columnIterator.hasNext(); i++) {
            DSDColumn column = columnIterator.next();
            inclusionMask[i] = !"value".equals(column.getSubject());
        }
        //Parse table data
        Set<String> words = new HashSet<>();
        for (Object[] row : resource.getData())
            for (int i=0; i<row.length; i++)
                if (inclusionMask[i] && row[i]!=null)
                    for (String word : getWords(row[i].toString()))
                        words.add(word);
        //Append table data
        for (String word : words)
            text.append(word).append(' ');
        //Return text
        return text.toString();
    }

    private String[] getWords (String text) {
        return text.toLowerCase().split("[\\s\\.\"\\!£$\\%\\&/\\(\\)\\='\\?\\^\\[\\]\\*\\+\\,;\\:]+");
    }

    private ODocument getIndexDocument(String id, ODatabaseDocument connection) {
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select from DataIndex where index|id = ?");
        List<ODocument> existingIndexes = connection.query(query, id);
        return existingIndexes!=null && existingIndexes.size()>0 ? existingIndexes.iterator().next() : new ODocument("DataIndex");
    }



    //Utils
    private String getId(MeIdentification metadata) {
        return getId(metadata.getUid(), metadata.getVersion());
    }
    private String getId(String uid, String version) {
        return uid+(version!=null && !version.trim().equals("") ? '|'+version : "");
    }


}