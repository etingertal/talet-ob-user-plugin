/**
 * Class that invokes the RequestBaskets External REST API
 */

class RequestBasketsConsumerApi {

    private String protocol;
    private String host;
    private String port;
    private String api;
    private String token;
    public String url;
    public final String PROPERTIES_FILE_PATH = "/opt/jfrog/artifactory/var/etc/artifactory/plugins/requestBaskets.properties"

    public void init(){

        File propertiesFile = new File(PROPERTIES_FILE_PATH)
        Properties currProps = new Properties()
        currProps.load(new FileReader(propertiesFile))
//        def configFile = new ConfigSlurper().parse(new File(PROPERTIES_FILE_PATH).toURL())
        protocol = currProps.protocol;
        host = currProps.host;
        port = currProps.port;
        api = currProps.api;
        token = currProps.token;
        url = protocol + '://' + host + ':' + port + '/' + api;
    }

    public  int invoke(String clientAddr){
        def post = new URL(url).openConnection();
        def message = '{"clientAddress":' + clientAddr + '}'
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "application/json")
        post.getOutputStream().write(message.getBytes("UTF-8"));
        return post.getResponseCode();
    }
}

upload {
    beforeUploadRequest { request, repoPath ->
        RequestBasketsConsumerApi requestBasketsConsumerApi = new RequestBasketsConsumerApi();
        requestBasketsConsumerApi.init();
        log.debug "RequestBaskets URL: " + requestBasketsConsumerApi.url
        String code = requestBasketsConsumerApi.invoke(request.getClientAddress());
        log.debug "RequestBaskets Code: " + code
    }
}