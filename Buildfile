require "install.rb"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "7.5.0-SNAPSHOT"

COMMON = [JSON_JAVA, JACKSON, SPRING_JSON, SPRING_ETC, SPRING_AOP, PXE_HIBERNATE, 
APACHE_COMMONS.values, XMLSCHEMA, PXE_XMLBEANS[:xmlbeans], WSDL4J, AXIOM, AXIS2.values, 
LOG4J, SLF4J.values, INTALIO_DEPLOY, BPMS_COMMON, ODE_LIBS[:odebpelschemas], 
ODE_LIBS[:odebpelruntime], ODE_LIBS[:odeutils], ODE_LIBS[:odebpelapi], ODE_LIBS[:odebpelobj],
SECURITY.values, BATIK, XERCES.values, FOP, ASPECTJ.values , ANT , ANT_LAUNCHER , DROOLS_TEMPLATE , BRE_RUNTIME.values, INTALIO_LICENSE, ORGANIZATION_MAPPING]

COMPILE = [ COMMON, SERVLET_API ] 

TEST = [ COMMON, JUNIT] 

PACKAGE = []

desc "BPMS Cases"
define "bpms-cases" do

  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.cases"

  compile.options.source = "1.6"
  compile.options.target = "1.6"

  intalio_version_file = CommonFunctions.getIntalioVersionFile();

  build_properties = {}
  build_properties['Implementation-Version'] = VERSION_NUMBER

  compile.with(COMPILE,TEMPO_COMMON,TEMPO[:daonutbolts], SECURITY[:nutbolts])
  test.with(TEST)

  if(File.exists?(intalio_version_file))
	meta_inf << file(intalio_version_file)
  end

  package(:jar).with :manifest=>manifest.merge(build_properties)
  package(:aar).with :libs => [  ]
    package(:aar).exclude("org*")
		package(:aar).exclude("*resources*")
end
