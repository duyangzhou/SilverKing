package com.ms.silverking.cloud.dht.management;

import com.ms.silverking.net.security.Authenticator;
import com.ms.silverking.net.security.NoopAuthenticatorImpl;
import org.kohsuke.args4j.Option;

import com.ms.silverking.cloud.dht.client.Compression;
import com.ms.silverking.cloud.dht.daemon.DHTNodeOptions;
import com.ms.silverking.cloud.dht.daemon.RingHealth;
import com.ms.silverking.cloud.dht.daemon.storage.NeverReapPolicy;
import com.ms.silverking.cloud.dht.daemon.storage.ReapMode;
import com.ms.silverking.cloud.dht.daemon.storage.ReapOnIdlePolicy;
import com.ms.silverking.cloud.dht.daemon.storage.ReapPolicy;
import com.ms.silverking.text.ObjectDefParser2;

class SKAdminOptions {
	static String	exclusionsTarget = "exclusions";
	static String	activeDaemonsTarget = "activeDaemons";
	
	static final int	skfsTimeoutNotSet = -1;
	
	SKAdminOptions() {
	}
	
	@Option(name="-g", usage="GridConfig", required=false)
	String	gridConfig;
	
	@Option(name="-G", usage="GridConfigBase", required=false)
	String	gridConfigBase;
	
	@Option(name="-c", usage="Command(s)", required=true)
	String	commands;
	
	@Option(name="-C", usage="Compression", required=false)
	Compression	compression = Compression.LZ4;
	
	@Option(name="-t", usage="target(s)", required=false)
	String	targets;
	
	boolean isReservedTarget(String s) {
		return s.equalsIgnoreCase(exclusionsTarget) || s.equalsIgnoreCase(activeDaemonsTarget);
	}
	
	boolean targetsEqualsExclusionsTarget() {
		return targets != null && targets.equalsIgnoreCase(exclusionsTarget);
	}
	
	boolean targetsEqualsActiveDaemonsTarget() {
		return targets != null && targets.equalsIgnoreCase(activeDaemonsTarget);
	}
	
	@Option(name="-e", usage="includeExcludedHosts", required=false)
	boolean	includeExcludedHosts;
	
	@Option(name="-L", usage="CoreLimit", required=false)
	String	coreLimit;
	
	@Option(name="-l", usage="LogLevel", required=false)
	String	logLevel = "WARNING";
	
	@Option(name="-cp", usage="ClassPath", required=false)
	String	classPath;
	
	@Option(name="-jb", usage="JavaBin", required=false)
	String	javaBinary;

	@Option(name="-ao", usage="AssertionOption", required=false)
	public String assertionOption = "-da";
	
	@Option(name="-po", usage="ProfilingOptions", required=false)
	public String profilingOptions = "";
	
	@Option(name="-wt", usage="NumWorkerThreads", required=false)
	public int numWorkerThreads = 6;
	
	@Option(name="-wto", usage="WorkerTimeoutSeconds", required=false)
	public int workerTimeoutSeconds = 5 * 60;
	
	@Option(name="-to", usage="TimeoutSeconds", required=false)
	public String timeoutSeconds = Integer.toString(3 * 60 * 60);
	
	@Option(name="-into", usage="InactiveNodeTimeoutSeconds", required=false)
	public int inactiveNodeTimeoutSeconds = DHTNodeOptions.defaultInactiveNodeTimeoutSeconds;
	
	@Option(name="-forceUnsafe", usage="forceInclusionOfUnsafeExcludedServers", required=false)
	boolean	forceInclusionOfUnsafeExcludedServers = false;
	
	@Option(name="-excludeInstanceExclusions", usage="excludeInstanceExclusions", required=false)
	boolean	excludeInstanceExclusions = false;
	
	@Option(name="-ma", usage="MaxAttempts", required=false)
	public int maxAttempts = 2;
	
	@Option(name="-D", usage="displayOnly", required=false)
	boolean	displayOnly;
	
	@Option(name="-fsdc", usage="forceSKFSDirectoryCreation", required=false)
	boolean	forceSKFSDirectoryCreation;
	
	@Option(name="-r", usage="disableReap", required=false)
	boolean disableReap = false;
	
	@Option(name="-reapMode", usage="reapMode", required=false)
	ReapMode reapMode = null;
	
	@Option(name="-reapPolicy", usage="reapPolicy", required=false)
	String reapPolicy = ObjectDefParser2.toClassAndDefString(new ReapOnIdlePolicy());	
	
	@Option(name="-ringHealth", usage="ringHealth", required=false)
	RingHealth	ringHealth;
	
	public ReapPolicy getReapPolicy() {
		if (disableReap) {
			return new NeverReapPolicy();
		} else {
			if (reapMode == null) {
				return (ReapPolicy)ObjectDefParser2.parse(reapPolicy, ReapPolicy.class.getPackage());
			} else {
				switch (reapMode) {
				case None:
					return NeverReapPolicy.instance;
				case OnStartup:
					return new ReapOnIdlePolicy().reapOnIdle(false);
				case OnIdle:
					return new ReapOnIdlePolicy().reapOnStartup(false);
				case OnStartupAndIdle:
					return new ReapOnIdlePolicy();
				default: throw new RuntimeException("Panic");
				}
			}
		}
	}

	@Option(name="-useAuthWithImpl", usage = "specify AuthenticatorImpl in SKObjectStringDef", required = false)
    private String _authImplSkStrDef = null;
    // Try to firstly parse it as Authenticator object in case of wrong StringDef given
	public Authenticator getAuthenticator() {
        if (_authImplSkStrDef == null) {
            return new NoopAuthenticatorImpl();
        } else {
            return Authenticator.parseSKDef(_authImplSkStrDef);
        }
    }

	@Option(name="-destructive", usage="destructive", required=false)
	boolean	destructive = false;
	
	@Option(name="-opTimeoutController", usage="opTimeoutController", required=false)
	public String opTimeoutController = "<OpSizeBasedTimeoutController>{maxAttempts=5,constantTime_ms=300000,itemTime_ms=305,nonKeyedOpMaxRelTimeout_ms=1200000}";

	@Option(name="-dirNSPutTimeoutController", usage="dirNSPutTimeoutController", required=false)
	public String dirNSPutTimeoutController = "<OpSizeBasedTimeoutController>{maxAttempts=12,constantTime_ms=60000,itemTime_ms=305,nonKeyedOpMaxRelTimeout_ms=1200000}";
	
	@Option(name="-fileBlockNSValueRetentionPolicy", usage="fileBlockNSValueRetentionPolicy", required=false)
	public String fileBlockNSValueRetentionPolicy = "valueRetentionPolicy=<ValidOrTimeAndVersionRetentionPolicy>{mode=wallClock,minVersions=0,timeSpanSeconds=300}";
	
	@Option(name="-defaultClassVars", usage="defaultClassVars", required=false)
	public String defaultClassVars;
	
	@Option(name="-explicitClassVarDef", usage="explicitClassVarDef", required=false)
	public String explicitClassVarDef;
	
	@Option(name="-ps", usage="PreferredServer", required=false)
	public String preferredServer;	
	
	@Option(name="-skfsEntryTimeoutSecs", usage="skfsEntryTimeoutSecs", required=false)
	public int	skfsEntryTimeoutSecs = skfsTimeoutNotSet;	
	
	@Option(name="-skfsAttrTimeoutSecs", usage="skfsAttrTimeoutSecs", required=false)
	public int	skfsAttrTimeoutSecs = skfsTimeoutNotSet;	
	
	@Option(name="-skfsNegativeTimeoutSecs", usage="skfsNegativeTimeoutSecs", required=false)
	public int	skfsNegativeTimeoutSecs = skfsTimeoutNotSet;
	
	@Option(name="-checkSKFSOptions", usage="checkSKFSOptions", required=false)
	public String checkSKFSOptions;
	
	@Option(name="-pinToNICLocalCPUs", usage="pinToNICLocalCPUs", required=false)
	public String pinToNICLocalCPUs;

	@Option(name="-uwc", usage="unsafeWarningCountdownSecs", required=false)
	int unsafeWarningCountdownSecs = 10;

	@Option(name="-sfoc", usage="sleepForeverOnCompletion", required=false)
	boolean sleepForeverOnCompletion = false;

	@Option(name = "-useAclWithImpl", usage = "specify ZooKeeperACLImpl in SKObjectStringDef", required = false)
    public String aclImplSkStrDef;

	@Option(name = "-startNodeWithExtraJVMOptions", usage = "enable user to append its customized JVM options when starting a DHTNode")
    private String _startNodeExtraJVMOptions;
	public String[] getStartNodeExtraJVMOptions() {
	    if (_startNodeExtraJVMOptions == null) {
	        return new String[0];
        }

        String trimmedOps =_startNodeExtraJVMOptions.trim();
        String[] options = trimmedOps.split("\\s+");
	    for(String op : options) {
	        // let it crash to prevent SKAdmin command to be populated
            if (!op.startsWith("-")) {
                throw new RuntimeException("User provides the invalid JVMOptions string [" + trimmedOps + "] where [" + op + "] shall at least start with '-'");
            }
        }
        return options;
    }
}
