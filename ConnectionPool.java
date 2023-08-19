// Connection Pool approach


public class ConnectionCacheProvider {
    private static final int MAX_POOL_SIZE = 10 ;
    private static final String DB_NAMESPACE = "namespace" ;
    private final Map<String, ConcurrentLinkedQueue<ConnectionInfo>> connectionsMap = new ConcurrentHashMap<>();
 
    private static Logger logger = Logger.getLogger(LogMessageId.SYSTEM);
 
    @Autowired
    private ConnectionManager connectionManager;
 
    @Autowired
    private dbStore dbStore;
 
 
    public ConnectionCacheProvider() {
    }
 
    public Connection getConnection(String Id) throws Exception {
        ConcurrentLinkedQueue<ConnectionInfo> queue = connectionsMap.get(Id);
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<>();
            connectionsMap.put(Id, queue);
        } else {
            for (ConnectionInfo connectionInfo : queue) { // can be checked here if connection is not in use.
                if (ConnectionInfo != null && ConnectionInfo.getVcConnectionFree()) {
                    queue.remove(connectionInfo);
                    connectionInfo.setConnectionFree(false);
                    queue.add(connectionInfo);
                    Connection connection = connectionInfo.getConnection();
                    if (connection != null && connection.isCurrentSessionActive()) {
                        return connectionInfo.getConnection();
                    }
                }
            }
        }
 
        queue = connectionsMap.get(Id);
 
        if (queue.size() < MAX_POOL_SIZE) {
            Connection connection = createNewConnection(Key);
            ConnectionInfo connectionInfo = new ConnectionInfo();
            ConnectionInfo.setConnection((Connection) connection);
            ConnectionInfo.setConnectionFree(false);
            return connection;
        }
 
        throw new Exception("Connection pool is full");
    }
 
    public void releaseConnection(String Id, Connection connection) {
        // add connection back to queue marking it as free.
        ConcurrentLinkedQueue<ConnectionInfo> queue = connectionsMap.get(Id);
        for (ConnectionInfo connectionInfo: queue) { // can be checked here if connection is not in use.
            Connection connection = connectionInfo.getConnection();
            if (!connection.equals(null)) {
                queue.remove(connectionInfo);
                connectionInfo.setConnectionFree(true);
                connectionInfo.setConnection(connection);
                queue.add(connectionInfo);
                logger.info("Added connection back to the queue");
                return;
            }
        }
    }
 
    public void close() throws Exception {
        for (ConcurrentLinkedQueue<ConnectionInfo> queue : connectionsMap.values()) {
            for (ConnectionInfo connection : queue) {
                Connection connection =  connection.getConnection();
                connection.logout();
                queue.remove(connection);
            }
        }
        connectionsMap.clear();
    }
 
 
    //  
    public void closeAllConnectionsForCM(String Id) {
        ConcurrentLinkedQueue<ConnectionInfo> queue = connectionsMap.get(computeManagerId);
        for (ConnectionInfo connection : queue) {
            Connection connection =  connection.getConnection();
            connection.logout();
            queue.remove(connection);
        }
    }
 
    private Connection createNewConnection(String Id) throws Exception {
        Connection connection;
        try (dbStore.connect()) {
            connection = ConnectionManager.getConnection(Id);
            dbStore.commit();
        }
        return Connection;
 
 
    }
}
 
 
 
public class ConnectionInfo {
 
    private Connection connection;
    private Boolean connectionFree; <--- this can be inside Connection as inUse
 
    public Connection getConnection() {
        return connection;
    }
 
    public Boolean getConnectionFree() {
        return connectionFree;
    }
 
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
 
    public void setConnectionFree(Boolean ConnectionFree) {
        this.ConnectionFree = ConnectionFree;
    }
 
}
