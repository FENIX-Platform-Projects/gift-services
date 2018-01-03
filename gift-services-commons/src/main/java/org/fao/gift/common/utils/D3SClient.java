package org.fao.gift.common.utils;

import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.msd.dto.data.ReplicationFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.commons.utils.Language;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@ApplicationScoped
public class D3SClient {
    @Inject
    org.fao.gift.common.utils.FenixUtils fenixUtils;


    public Collection<Code> filterCodelist(String baseUrl, String uid, String version, Collection<String> codes) throws Exception {
        return filterCodelist(baseUrl, uid, version, codes, 1);
    }

    public Collection<Code> filterCodelist(String baseUrl, String uid, String version, Collection<String> codes, Integer levels) throws Exception {
        //Create filter
        CodesFilter filter = new CodesFilter();

        filter.uid = uid;
        filter.version = version;
        filter.codes = codes;
        filter.levels = levels;

        return filterCodelist(baseUrl, filter);

    }

    public Collection<Code> filterCodelist(String baseUrl, CodesFilter filter) throws Exception {
        //Check parameters
        if (filter == null || filter.uid == null)
            throw new BadRequestException();
        //Send request
        Map<String, String> parameters = new HashMap<>();
        parameters.put("maxSize", "1000000");
        String url = addQueryParameters(baseUrl + "msd/codes/filter", parameters);
        Response response = sendRequest(url, filter, "post");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S filtering codelist " + filter.uid);

        //Parse response
        return response.getStatus() != 204 ? response.readEntity(new GenericType<Collection<Code>>() {
        }) : new LinkedList<Code>();
    }

    public Resource<DSDDataset, Object[]> getDataset(String baseUrl, String uid, String version, Language language, Integer perPage, Integer page) throws Exception {
        String responseBody = getResource(baseUrl, uid, version, language, perPage, page);
        return JSONUtils.decode(responseBody, Resource.class, DSDDataset.class, Object[].class);
    }

    public Resource<DSDCodelist, Code> getCodelist(String baseUrl, String uid, String version) throws Exception {
        String responseBody = getResource(baseUrl, uid, version, null, null, null);
        return JSONUtils.decode(responseBody, Resource.class, DSDCodelist.class, Code.class);
    }

    private String getResource(String baseUrl, String uid, String version, Language language, Integer perPage, Integer page) throws Exception {
        //Create URL
        StringBuilder url = new StringBuilder(baseUrl).append("msd/resources/");
        if (version == null)
            url.append("uid/");
        url.append(uid);
        if (version != null)
            url.append('/').append(version);
        //Create query parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dsd", "true");
        parameters.put("full", "true");
        if (language != null)
            parameters.put("language", language.getCode());
        if (perPage != null) {
            parameters.put("perPage", perPage.toString());
            if (page != null)
                parameters.put("page", page.toString());
        }
        //Send request
        Response response = sendRequest(addQueryParameters(url.toString(), parameters), null, "get");
        if (response.getStatus() != 200)
            throw new Exception("Error from D3S loading resource");
        //Parse responseObjectMapper
        return response.readEntity(String.class);
    }


    //TMP

    public Resource<DSDDataset, Object[]> getCstatDataset(String baseUrl, String uid, String version) throws Exception {
        String responseBody = getCstatResource(baseUrl, uid, version);
        return JSONUtils.decode(responseBody, Resource.class, DSDDataset.class, Object[].class);
    }

    private String getCstatResource(String baseUrl, String uid, String version) throws Exception {
        //Create URL
        StringBuilder url = new StringBuilder(baseUrl).append("msd/resources/");
        if (version == null)
            url.append("uid/");
        url.append(uid);
        if (version != null)
            url.append('/').append(version);
        //Create query parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dsd", "true");
        parameters.put("full", "true");
        parameters.put("language", "EN,FR");
        //Send request
        Response response = sendRequest(addQueryParameters(url.toString(), parameters), null, "get");
        if (response.getStatus() != 200)
            throw new Exception("Error from D3S loading resource");
        //Parse responseObjectMapper
        return response.readEntity(String.class);
    }

    //END-TMP


    public MeIdentification<DSDDataset> getDatasetMetadata(String baseUrl, String uid, String version) throws Exception {
        String responseBody = getMetadata(baseUrl, uid, version);
        return JSONUtils.decode(responseBody, MeIdentification.class, DSDDataset.class);
    }

    private String getMetadata(String baseUrl, String uid, String version) throws Exception {
        //Create URL
        StringBuilder url = new StringBuilder(baseUrl).append("msd/resources/metadata/");
        if (version == null)
            url.append("uid/");
        url.append(uid);
        if (version != null)
            url.append('/').append(version);
        //Create query parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dsd", "true");
        parameters.put("full", "true");
        //Send request
        Response response = sendRequest(addQueryParameters(url.toString(), parameters), null, "get");
        if (response.getStatus() != 200)
            throw new Exception("Error from D3S loading resource (" + uid + " - " + version + "): (" + response.getStatus() + ") " + response.getEntity());
        //Parse responseObjectMapper
        return response.readEntity(String.class);
    }


    public Collection<MeIdentification<DSDDataset>> retrieveMetadata(String baseUrl, String context) throws Exception {
        //Create filter
        StandardFilter filter = new StandardFilter();

        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList(context);
        filter.put("dsd.contextSystem", fieldFilter);

        fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList("dataset");
        filter.put("meContent.resourceRepresentationType", fieldFilter);

        //Send request
        Map<String, String> parameters = new HashMap<>();
        parameters.put("maxSize", "1000000");
        String url = addQueryParameters(baseUrl + "msd/resources/find", parameters);
        Response response = sendRequest(url, filter, "post");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S requiring existing datasets metadata");

        //Parse response
        return response.getStatus() != 204 ? response.readEntity(new GenericType<Collection<MeIdentification<DSDDataset>>>() {
        }) : new LinkedList<MeIdentification<DSDDataset>>();
    }


    public void insertMetadata(String baseUrl, Collection<MeIdentification<DSDDataset>> metadataList) throws Exception {
        if (metadataList == null || metadataList.size() == 0)
            return;
        //Send requests
        for (Collection<MeIdentification<DSDDataset>> segment : splitCollection(metadataList, 25)) {
            Response response = sendRequest(baseUrl + "msd/resources/massive", segment, "post");
            if (response.getStatus() != 200 && response.getStatus() != 201)
                throw new Exception("Error from D3S adding datasets metadata");
        }
    }


    public Response insertMetadata(String baseUrl, MeIdentification<DSDDataset> metadata) throws Exception {
        //Send requests
        return sendRequest(baseUrl + "/msd/resources/metadata", metadata, "post");
    }


    public void updateMetadata(String baseUrl, Collection<MeIdentification<DSDDataset>> metadataList) throws Exception {
        if (metadataList == null || metadataList.size() == 0)
            return;
        //Send request
        for (Collection<MeIdentification<DSDDataset>> segment : splitCollection(metadataList, 25)) {
            Response response = sendRequest(baseUrl + "msd/resources/massive", segment, "put");
            if (response.getStatus() != 200 && response.getStatus() != 201)
                throw new Exception("Error from D3S adding datasets metadata");
        }
    }

    public Response updateMetadata(String baseUrl, MeIdentification<DSDDataset> metadata) throws Exception {
        //Send request
        Response response = sendRequest(baseUrl + "msd/resources/metadata", metadata, "put");
        if (response.getStatus() != 200 && response.getStatus() != 201)
            throw new Exception("Error from D3S adding datasets metadata");
        return response;

    }

    public void deleteMetadata(String baseUrl, Collection<MeIdentification<DSDDataset>> metadataList) throws Exception {
        if (metadataList == null || metadataList.size() == 0)
            return;

        //Create filter
        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.ids = new LinkedList<>();
        for (MeIdentification<DSDDataset> metadata : metadataList)
            fieldFilter.ids.add(new IdFilter(metadata.getUid(), metadata.getVersion()));
        StandardFilter filter = new StandardFilter();
        filter.put("id", fieldFilter);

        //Send request
        Response response = sendRequest(baseUrl + "msd/resources/massive/delete", filter, "post");
        if (response.getStatus() != 200 && response.getStatus() != 201)
            throw new Exception("Error from D3S requiring existing datasets metadata");
    }


    public Response deleteMetadata(String baseUrl, String uid, String version) throws Exception {
        //Send request
        String methodToDelete = version == null? "msd/resources/metadata/uid/"+uid : "msd/resources/metadata/"+uid+"/"+version;
        Response response = sendRequest(baseUrl + "msd/resources/massive/delete", null, "delete");
        if (response.getStatus() != 200 && response.getStatus() != 201)
            throw new Exception("Error from D3S requiring existing datasets metadata");
        return response;
    }

    public void updateCodelists(String baseUrl, Collection<Resource<DSDCodelist, Code>> resourceList) throws Exception {
        if (resourceList == null || resourceList.size() == 0)
            return;
        //Send request
        for (Resource<DSDCodelist, Code> resource : resourceList) {
            Response response = sendRequest(baseUrl + "msd/resources", resource, "put");
            if (response.getStatus() != 200 && response.getStatus() != 201)
                throw new Exception("Error from D3S updating codelist " + resource.getMetadata().getUid());
        }
    }

    public void appendDatasetMetadata(String baseUrl, MeIdentification<DSDDataset> metadata) throws Exception {
        //Send request
        Response response = sendRequest(baseUrl + "msd/resources/metadata", metadata, "patch");
        if (response.getStatus() == 204)
            throw new NoContentException("Metadata not found: " + metadata.getUid() + (metadata.getVersion() != null ? "-" + metadata.getVersion() : ""));
        if (response.getStatus() != 200 && response.getStatus() != 201)
            throw new Exception("Error from D3S updating dataset metadata last update date");
    }

    public void updateDatasetMetadataUpdateDate(String baseUrl, String contextSystem) throws Exception {
        if (contextSystem == null)
            return;
        //Create filter
        StandardFilter filter = new StandardFilter();

        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList(contextSystem);
        filter.put("dsd.contextSystem", fieldFilter);

        fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList("dataset");
        filter.put("meContent.resourceRepresentationType", fieldFilter);

        MeIdentification<DSDDataset> metadata = new MeIdentification<>();
        MeMaintenance meMaintenance = new MeMaintenance();
        metadata.setMeMaintenance(meMaintenance);
        SeUpdate seUpdate = new SeUpdate();
        seUpdate.setUpdateDate(new Date());
        meMaintenance.setSeUpdate(seUpdate);

        ReplicationFilter<DSDDataset> updateFilter = new ReplicationFilter<>();
        updateFilter.setFilter(filter);
        updateFilter.setMetadata(metadata);

        //Send request
        Response response = sendRequest(baseUrl + "msd/resources/replication", updateFilter, "patch");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S updating datasets metadata last update date");
    }

    public void updateMetadata(String baseUrl, MeIdentification metadata, boolean override) throws Exception {
        //Send request
        Response response = sendRequest(baseUrl + "msd/resources/metadata", metadata, override ? "put" : "patch");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S updating datasets metadata");
    }


    public Collection<MeIdentification<DSDDataset>> retrieveMetadataForStatistics(String baseUrl, String context,
                                                                                  CodesFilter countries, CodesFilter referenceArea, CodesFilter coverageSector, TimeFilter year, CodesFilter confidentialityStatus) throws Exception {
        //Create filter
        StandardFilter filter = new StandardFilter();

        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList(context);
        filter.put("dsd.contextSystem", fieldFilter);

        fieldFilter = new FieldFilter();
        fieldFilter.enumeration = Arrays.asList("dataset");
        filter.put("meContent.resourceRepresentationType", fieldFilter);

        if (countries != null && countries.codes != null && countries.codes.size() > 0) {
            fieldFilter = new FieldFilter();
            fieldFilter.codes = Arrays.asList(countries);
            filter.put("meContent.seCoverage.coverageGeographic", fieldFilter);
        }

        if (year != null) {
            fieldFilter = new FieldFilter();
            fieldFilter.time = Arrays.asList(year);
            filter.put("meContent.seCoverage.coverageTime", fieldFilter);
        }

        if (coverageSector != null && coverageSector.codes != null && coverageSector.codes.size() > 0) {
            Collection codes = new LinkedList();
            for (String code : coverageSector.codes)
                if ("1".equals(code))
                    codes.addAll(Arrays.asList("1", "3"));
                else if ("2".equals(code))
                    codes.addAll(Arrays.asList("2", "3"));
            if ((coverageSector.codes = codes).size() > 0) {
                fieldFilter = new FieldFilter();
                fieldFilter.codes = Arrays.asList(coverageSector);
                filter.put("meContent.seCoverage.coverageSectors", fieldFilter);
            }
        }

        if (referenceArea != null && referenceArea.codes != null && referenceArea.codes.size() > 0) {
            Collection codes = new LinkedList();
            for (String code : referenceArea.codes)
                if ("1".equals(code))
                    codes.addAll(Arrays.asList("1"));
                else if ("2".equals(code))
                    codes.addAll(Arrays.asList("2", "3", "4", "5"));
            if ((referenceArea.codes = codes).size() > 0) {
                fieldFilter = new FieldFilter();
                fieldFilter.codes = Arrays.asList(referenceArea);
                filter.put("meContent.seReferencePopulation.referenceArea", fieldFilter);
            }
        }

        if (confidentialityStatus != null && confidentialityStatus.codes != null && confidentialityStatus.codes.size() > 0) {
            Collection codes = new LinkedList();
            codes.addAll(confidentialityStatus.codes);

            if ((confidentialityStatus.codes = codes).size() > 0) {
                fieldFilter = new FieldFilter();
                fieldFilter.codes = Arrays.asList(confidentialityStatus);
                filter.put("meAccessibility.seConfidentiality.confidentialityStatus", fieldFilter);
            }
        }


        //Send request
        Map<String, String> parameters = new HashMap<>();
        parameters.put("maxSize", "1000000");
        parameters.put("full", "true");
        parameters.put("dsd", "true");
        String url = addQueryParameters(baseUrl + "msd/resources/find", parameters);
        Response response = sendRequest(url, filter, "post");
        if (response.getStatus() != 200 && response.getStatus() != 201 && response.getStatus() != 204)
            throw new Exception("Error from D3S requiring existing datasets metadata");

        //Parse response
        return response.getStatus() != 204 ? response.readEntity(new GenericType<Collection<MeIdentification<DSDDataset>>>() {
        }) : new LinkedList<MeIdentification<DSDDataset>>();
    }


    private Response sendRequest(String url, Object entity, String method) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        return entity != null ?
                target.request(MediaType.APPLICATION_JSON_TYPE).build(method.trim().toUpperCase(), javax.ws.rs.client.Entity.json(entity)).invoke() :
                target.request(MediaType.APPLICATION_JSON_TYPE).build(method.trim().toUpperCase()).invoke();
    }

    private String addQueryParameters(String url, Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(url);
        if (parameters != null && parameters.size() > 0) {
            sb.append('?');
            for (Map.Entry<String, String> parameter : parameters.entrySet())
                sb.append(URLEncoder.encode(parameter.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(parameter.getValue(), "UTF-8")).append('&');
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    private <T> Collection<Collection<T>> splitCollection(Collection<T> list, int segmentSize) {
        if (list == null)
            return null;
        Collection<Collection<T>> buffer = new LinkedList<>();
        Collection<T> segment = new LinkedList<>();
        int count = 0;
        for (T element : list) {
            if (++count > segmentSize) {
                buffer.add(segment);
                segment = new LinkedList<>();
                count = 0;
            }
            segment.add(element);
        }
        if (segment.size() > 0)
            buffer.add(segment);
        return buffer;
    }


}






/*
    public static void main(String[] args) throws Exception {
        D3SClient client = new D3SClient();
        Collection<MeIdentification<DSDDataset>> metadata = client.retrieveMetadata("http://localhost:7777/v2/");
        System.out.println(metadata.size());
//        client.deleteMetadata("http://localhost:7777/v2/",metadata);
    }
*/
