folder('wonderland-twiddle') {
  properties {
    folderCredentialsProperty{
      domainCredentials {
        domainCredentials {
          domain {
            name('')
            description('')
          }
          credentials {
            usernamePassword {
              scope('GLOBAL')
              id('flag6')
              description('')
              username('flag6')
              password("${FLAG6}")
            }
          }
        }
      }
    }
  }
}
job('wonderland-twiddle/wonderland-twiddledum') {
  label('agent1')
  scm {
    git {
      remote {
        url('http://gitea:3000/Wonderland/twiddledum.git')
      }
      branch('main')
    }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('USERNAME', 'FLAG6', 'flag6')
    }
  }
  steps {
    shell('''npm ci --ignore-scripts
    node index.js''')
    shell('''echo "//registry.npmjs.org/:_authToken=${FLAG6}" >> ~/.npmrc
    npm publish || true''')
  }
}