pipeline {
    agent {
        // 此处设定构建环境，目前可选有
        // default, java-8, python-3.5, ruby-2.3, go-1.11 等
        // 详情请阅 https://dev.tencent.com/help/knowledge-base/how-to-use-ci#agents
        label "java-8"
    }
    stages  {
        
        stage("检出") {
            steps {
                sh 'ci-init'
                    [
                        $class: 'GitSCM',
                        branches: [[name: env.GIT_BUILD_REF]],
                        userRemoteConfigs: [[url: env.GIT_REPO_URL, credentialsId: env.CREDENTIALS_ID]]
                    ]
                )
            }
        }

        stage("构建") {
            steps {
                echo "构建中..."
                // 请在这里放置您项目代码的单元测试调用过程，例如:
                sh 'gradlew clean shadowJar' // mvn 示例
                // sh 'make' // make 示例
                echo "构建完成."
                archiveArtifacts artifacts: '**/build/*.jar', fingerprint: true // 收集构建产物
            }
        }

    }
}