pipelineJob('wonderland-dodo') {
  definition {
    cpsScm {
      scm {
        git {
          remote {
            url('http://gitea:3000/Wonderland/dodo.git')
          }
          branch('main')
        }
      }
    }
    cpsFlowDefinition {
      sandbox(true)
      script('''pipeline {
    agent any

    stages {
        stage ('Checkout') {
            steps {
                git branch: 'main', url: 'http://gitea:3000/Wonderland/dodo.git'
            }
        }

        stage ('Security Scan') {
            steps {
                sh \'\'\'
                    # Run Checkov security checks
                    checkov -d . --check CKV2_AWS_39,CKV2_AWS_38,CKV_AWS_20,CKV_AWS_57
                    
                    # Scan for dangerous Terraform provisioners that can execute arbitrary code
                    echo "Scanning for dangerous Terraform provisioners..."
                    if grep -r -E "(local-exec|remote-exec)" --include="*.tf" .; then
                        echo "ERROR: Detected dangerous provisioner (local-exec or remote-exec) in Terraform code"
                        echo "Provisioners that execute arbitrary commands are not allowed for security reasons"
                        exit 1
                    fi
                    
                    # Scan for null_resource which is commonly used with provisioners
                    if grep -r "null_resource" --include="*.tf" .; then
                        echo "ERROR: Detected null_resource in Terraform code"
                        echo "null_resource is commonly used with command-executing provisioners and is not allowed"
                        exit 1
                    fi
                    
                    # Scan for external data sources that can execute commands
                    if grep -r "external" --include="*.tf" . | grep -E "data\\s+\\"external\\""; then
                        echo "ERROR: Detected external data source in Terraform code"
                        echo "External data sources can execute arbitrary commands and are not allowed"
                        exit 1
                    fi
                    
                    echo "Security scan passed - no dangerous provisioners detected"
                \'\'\'
            }
        }

        stage ('Plan') {
            steps {
                sh \'\'\'
                    terraform init -no-color
                    terraform plan -no-color -out=tfplan
                \'\'\'
            }
        }

        stage ('Approval') {
            steps {
                script {
                    input message: 'Review the Terraform plan above. Approve deployment?', 
                          ok: 'Deploy',
                          submitter: 'admin'
                }
            }
        }

        stage ('Apply') {
            steps {
                sh \'\'\'
                    terraform apply -no-color tfplan
                    res=`awslocal --endpoint-url=http://localstack:4566 s3api get-bucket-acl --bucket dodo | jq '.Grants[] | select(.Grantee.Type == "Group" and .Grantee.URI == "http://acs.amazonaws.com/groups/global/AllUsers" and .Permission == "READ")' &> /dev/null`
                    if [ -z "$res" ]
                    then
                        echo "Secure"
                    else
                        echo "FLAG7: A62F0E52-7D67-410E-8279-32447ADAD916"
                    fi
                \'\'\'
            }
        }

    }
    post {
        always {
            cleanWs()
        }
    }
}'''.stripIndent())
    }
  }
}
