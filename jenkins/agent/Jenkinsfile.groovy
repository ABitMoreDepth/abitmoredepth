pipeline {
  agent {
    docker {
      image 'ci-agent'
      label 'docker'
    }
  }

  options {
    ansiColor('xterm')
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '50')
    retry(3)
    disableConcurrentBuilds()
  }

  stages {
    stage('SCM Checkout') {
      steps {
         checkout scm
      }
    }

    stage('Build') {
      steps {
        script {
          dir('jenkins/agent') {
            docker.build('114151591996.dkr.ecr.eu-west-1.amazonaws.com/abmd/ci-agent', '--pull --no-cache .')
          }
        }
      }
    }

    stage('Publish') {
      steps {
        script {
          dir('jenkins/agent') {
            docker.withRegistry('114151591996.dkr.ecr.eu-west-1.amazonaws.com/abmd') {
              docker.image('114151591996.dkr.ecr.eu-west-1.amazonaws.com/abmd/ci-agent:latest').push('latest')
            }
          }
        }
      }
    }
  }
}

