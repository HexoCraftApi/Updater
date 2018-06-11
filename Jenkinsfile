pipeline {
  agent {
    docker {
      image 'craftdock/maven:3.5-jdk8'
      args '-v /volume1/docker/.m2:/var/maven_home/.m2'
    }

  }
  stages {
    stage('Init') {
      steps {
        echo 'This is the init stage'
      }
    }
    stage('Build') {
      steps {
        sh 'mvn clean install'
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts 'target/*.jar'
      }
    }
  }
}