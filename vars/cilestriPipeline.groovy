def call(Map config = [:]) {
    def pipelineType = config.type ?: 'maven'
    def commands = config.commands ?: ['clean', 'compile', 'test']

    pipeline {
        agent any

        parameters {
            choice(
                name: 'RUN_TESTS',
                choices: ['yes', 'no'],
                description: 'Jalankan stage test?'
            )
        }

        environment {
            PIPELINE_TYPE = "${pipelineType}"
        }

        stages {
            stage('Validate Configuration') {
                steps {
                    script {
                        if (PIPELINE_TYPE != 'maven') {
                            error("Unsupported type: ${PIPELINE_TYPE}")
                        }
                    }
                }
            }

            stage('Maven Clean') {
                when {
                    expression { PIPELINE_TYPE == 'maven' && commands.contains('clean') }
                }
                steps {
                    script {
                        maven(['clean'])
                    }
                }
            }

            stage('Maven Compile') {
                when {
                    expression { PIPELINE_TYPE == 'maven' && commands.contains('compile') }
                }
                steps {
                    script {
                        maven(['compile'])
                    }
                }
            }

            stage('Maven Test') {
                when {
                    expression {
                        PIPELINE_TYPE == 'maven' &&
                            commands.contains('test') &&
                            params.RUN_TESTS == 'yes'
                    }
                }
                steps {
                    script {
                        maven(['test'])
                    }
                }
            }
        }

        post {
            success {
                echo 'Pipeline berhasil.'
            }
            failure {
                echo 'Pipeline gagal.'
            }
            always {
                echo "Status akhir: ${currentBuild.currentResult}"
            }
        }
    }
}
