def rtMaven = Artifactory.newMavenBuild()
def buildInfo = Artifactory.newBuildInfo()

buildInfo.env.capture = true
pipeline {
    agent {
        label 'rhel7_2||rhel7_3||rhel7'
    }

    stages {

        stage('Build DGE-Batch code') {
            steps {
				dir('.') {
					script {
						def pom = readMavenPom file: 'pom.xml'
						env['pomVersion'] = pom.version
						sh 'echo ${pomVersion}; oldVersion=${pomVersion}; newVersion=`echo ${oldVersion/SNAPSHOT/}${BUILD_NUMBER}`; currenDir=`pwd`; if [[ ${currenDir} = *"_master"* ]]; then mvn versions:set -DnewVersion=${newVersion}; fi'
						sh 'mvn clean install -Dcountry=TH -Denvironment=SIT'
					}
				}
			}
        }

        stage('SonarQube DGE-Batch code scan') {
            steps {
                withSonarQubeEnv('SonarQube') {
                      // requires SonarQube Scanner for Maven 3.2+
                       sh 'mvn sonar:sonar'
                }
            }
        }
        stage('Publish DGE-Batch binaries to Artifactory') {
            steps {
                script {
                    def server = Artifactory.server('artifactory-server')
                    def pom = readMavenPom file: 'pom.xml'
					def majorMinor = pom.version.substring(0, pom.version.lastIndexOf("-"))
                    def uploadSpec = """{
                      "files":[
                            {
                            "pattern": "cpm-*/target/*.tar.gz",
                            "target": "dge-batch-binaries/${majorMinor}/#${BUILD_NUMBER}/"
                            },
                            {
                            "pattern": "meniga-*/target/*.jar",
                            "target": "dge-batch-binaries/${majorMinor}/#${BUILD_NUMBER}/"
                            }

                        ]
                    }"""

                    buildInfo = server.upload(uploadSpec)
                    server.publishBuildInfo(buildInfo)
                }
            }
        }
    }
}