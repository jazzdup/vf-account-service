package com.vodafone.charging.accountservice.configuration;

public class VfCommonConstants {
    // HttpClient properties for connection pool
    public static final String HTTPCLIENT_DEFAULT_MAX_CONNECTIONS_PER_HOST	= "httpclient.default.max.connections.per.host" ;
    public static final String HTTPCLIENT_MAX_TOTAL_CONNECTIONS				= "httpclient.max.total.connections";
    public static final String HTTPCLIENT_SO_TIMEOUT						= "httpclient.so.timeout";
    public static final String HTTPCLIENT_CONNECTION_TIMEOUT				= "httpclient.connection.timeout";
    public static final String HTTPCLIENT_TCP_NO_DELAY						= "httpclient.tcp.no.delay";
    public static final String HTTPCLIENT_SEND_BUFFER_SIZE					= "httpclient.send.buffer.size";
    public static final String HTTPCLIENT_RECEIVE_BUFFER_SIZE				= "httpclient.receive.buffer.size";
    public static final String HTTPCLIENT_STALE_CONNECTION_CHECK			= "httpclient.stale.connection.check";
    public static final String HTTPCLIENT_POOL_OBJECT_TIMEOUT				= "httpclient.pool.object.timeout";
    // eviction thread
    public static final String EVICTION_THREAD_TIMEOUT_INTERVAL				= "eviction.thread.timeout.interval"; // (ms)
    public static final String EVICTION_THREAD_CONNECTION_TIMEOUT			= "eviction.thread.connection.timeout";	// (ms) optional

    public static final String HEADER_BASIC_AUTH_USERNAME 					= "vfg.default.gig.basic.auth.username";
    public static final String HEADER_BASIC_AUTH_PASSWORD 					= "vfg.default.gig.basic.auth.password";
    public static final String HEADER_VF_INT_TRACK_ID						= "vfg.default.gig.int.track.id";
    public static final String HEADER_APPLICATION_ID 						= "vfg.default.gig.application.id";
    public static final String HEADER_PARTNER_ID							= "vfg.default.gig.partner.id";
    public static final String HEADER_CI_TYPE								= "vfg.default.gig.ci.type";
    public static final String HEADER_CI_IDENTIFIER							= "vfg.default.gig.ci.identifier";
    public static final String HEADER_TI_SERVICE_ID							= "vfg.default.gig.ti.serviceid";
    public static final String HEADER_TI_VERSION							= "vfg.default.gig.ti.version";
    public static final String HEADER_TIMESTAMP_FORMAT						= "vfg.default.gig.timestamp.format";
}
