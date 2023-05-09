# skimmands-uploader

### current version: 0.1.0

## Config Settings

Config can be adjusted in the batch file `skimmands_uploader_start.bat`

### Flags

- -Xmx1g: max heap size 1Gb
- -DsyncInterval: sync interval in minutes between SQLite and MongoDB Atlas (default: 15)
- -DsqlitePath: file path to the phantombot SQLite database (default: "config/phantombot.db")
- -DmongoConnStr: connection string for MongoDB Atlas (default: "mongodb+srv://[username]:[password]@[hostname].net/&authMechanism=SCRAM-SHA-256&retryWrites=true&w=majority")


### Logs

A directory, `skimmands_uploader_logs`, will be created at the same location the .jar is run from. Up to 5 of the most recent log files will be stored here.
