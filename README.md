# skimmands-uploader

### current version: 0.1.0

## Config Settings

### SYNC
time in minutes to check local DB and sync with cloud  
value must be an integer

- syncInterval=15

### SQLITE
Path to SQLite database relative to the .exe  
.path can be directed to other directories *(eg. C:/Documents/my phantombot/PhantomBot-<version>/config/phantombot.db)*
- phantombotDB.path=phantombot.db
- phantombotDB.query=SELECT variable, value FROM phantombot_command

### MONGODB REALM
connection and auth to mongoDB Realm  
~*For version 0.1.0 only mongodbRealm.connection should be touched, ignore the rest of the fields*    
  

connection -> mongodb+srv://<username>:<password>@<hostname>.net/&authMechanism=SCRAM-SHA-256&retryWrites=true&w=majority  
mode -> (anon / email / facebook / google / apple / apikey / jwt / fn)  
auth.u -> username  
auth.p -> password  
mongodbRealm.connection=mongodb+srv://<username>:<password>@<hostname>.net/&authMechanism=SCRAM-SHA-256&retryWrites=true&w=majority  
mongodbRealm.mode=apikey  
uncomment if using email mode  
mongodbRealm.auth.u=username  
mongodbRealm.auth.p=password  
mongodbRealm.auth.pubkey=<API Key>
