pipeline {
    agent any
    environment {
        DOCKER_IMAGE = "givumethemoney"
        DOCKER_CREDENTIALS_ID = "docker-hub"
    }

    stages {
        stage('Clone') {
            steps {
                dir('/var/jenkins_home/workspace/givumethemoney'){
          			sh '''
          				echo delete existing project file
          			'''
          			deleteDir()
          		}
            echo 'Clonning Repository'
            git url: 'git@github.com:givumethemoney/givumethemoney.git',
              branch: 'test',
              credentialsId: 'github'
            }
            post {
             success {
               echo 'Successfully Cloned Repository'
             }
           	 failure {
               error 'This pipeline stops here...'
             }
          }
        }
        stage('Build') {
            steps {
                sh "chmod +x ./gradlew"
                sh "./gradlew clean build"
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }
        stage('Deploy') {
            steps {
                sshagent(credentials: ['operation']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no root@34.64.104.188 uptime
                        scp -o StrictHostKeyChecking=no /var/jenkins_home/workspace/gmtm-test/build/libs/givumethemoney-0.0.1-SNAPSHOT.jar root@34.64.104.188:/home/ubuntu/givumethemoney
                        ssh -o StrictHostKeyChecking=no -t root@34.64.104.188 /home/ubuntu/givumethemoney/deploy.sh
                    '''
                }
            }
        }
    }
    post {
        success {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD')]) {
                        discordSend description: """
                        제목 : ${currentBuild.displayName}
                        결과 : ${currentBuild.result}
                        실행 시간 : ${currentBuild.duration / 1000}s
                        """,
                        link: env.BUILD_URL, result: currentBuild.currentResult,
                        title: "${env.JOB_NAME} : ${currentBuild.displayName} 성공",
                        webhookURL: "$DISCORD"
            }
        }
        failure {
            withCredentials([string(credentialsId: 'discord-webhook', variable: 'DISCORD')]) {
                        discordSend description: """
                        제목 : ${currentBuild.displayName}
                        결과 : ${currentBuild.result}
                        실행 시간 : ${currentBuild.duration / 1000}s
                        """,
                        link: env.BUILD_URL, result: currentBuild.currentResult,
                        title: "${env.JOB_NAME} : ${currentBuild.displayName} 실패",
                        webhookURL: "$DISCORD"
            }
        }
    }
}