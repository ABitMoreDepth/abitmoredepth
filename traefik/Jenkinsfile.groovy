pipeline {
  agent {
    docker {
      image 'registry.abitmoredepth.com/ci-agent'
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
          dir('traefik') {
            docker.build('registry.abitmoredepth.com/traefik', '--pull --no-cache .')
          }
        }
      }
    }

    stage('Publish') {
      steps {
        script {
          dir('traefik') {
            docker.withRegistry('https://registry.abitmoredepth.com') {
              docker.image('registry.abitmoredepth.com/traefik').push('latest')
            }
          }
        }
      }
    }
  }
}

