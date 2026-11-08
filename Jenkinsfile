pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
        nodejs 'node20'
    }

    environment {
        DOCKER_REPO = 'kiransai006'
        // Private IP of the jingle-tools EC2 instance (SonarQube + Nexus)
        NEXUS_URL   = 'http://172.31.33.207:8081'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('Prepare') {
            steps {
                script {
                    def shortSha = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${shortSha}"
                }
                echo "Image tag: ${env.IMAGE_TAG}"
            }
        }

        stage('Backend: Build & Unit Test') {
            steps {
                dir('backend') {
                    sh 'mvn -B clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Frontend: Build & Test') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm test'
                    sh 'npm run build'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    dir('backend') {
                        sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389:sonar -Dsonar.projectKey=jingle-backend -Dsonar.projectName=jingle-backend'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Publish Artifact to Nexus') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    dir('backend') {
                        sh 'mvn -B -s "$MAVEN_SETTINGS" deploy -DskipTests -Dnexus.url=$NEXUS_URL'
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $DOCKER_REPO/jingle-backend:$IMAGE_TAG backend'
                sh 'docker build -t $DOCKER_REPO/jingle-frontend:$IMAGE_TAG frontend'
            }
        }

        stage('Push Images to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_TOKEN')]) {
                    sh 'echo "$DH_TOKEN" | docker login -u "$DH_USER" --password-stdin'
                    sh '''
                        for app in jingle-backend jingle-frontend; do
                            docker tag  $DOCKER_REPO/$app:$IMAGE_TAG $DOCKER_REPO/$app:latest
                            docker push $DOCKER_REPO/$app:$IMAGE_TAG
                            docker push $DOCKER_REPO/$app:latest
                        done
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes (Helm)') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG'),
                                 string(credentialsId: 'jingle-db-password', variable: 'DB_PASSWORD')]) {
                    sh '''
                        helm lint charts/jingle
                        helm upgrade --install jingle charts/jingle \
                          --namespace jingle --create-namespace \
                          --set backend.image.tag=$IMAGE_TAG \
                          --set frontend.image.tag=$IMAGE_TAG \
                          --set postgres.password="$DB_PASSWORD" \
                          --wait --timeout 8m
                        kubectl -n jingle get pods,svc,pvc
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Deployed ${env.DOCKER_REPO}/jingle-backend:${env.IMAGE_TAG} and ${env.DOCKER_REPO}/jingle-frontend:${env.IMAGE_TAG} to namespace 'jingle'"
        }
        failure {
            echo 'Pipeline failed - check the stage logs above.'
        }
        always {
            sh 'docker logout || true'
            sh 'docker image prune -f || true'
        }
    }
}