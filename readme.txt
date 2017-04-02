GET http://localhost:8080/demoSpringMvc4/nativequery?query=select * from Item a;&className=com.ratata.model.Item
GET http://localhost:8080/demoSpringMvc4/nativequery?query=Select i.data,i.id from Item i where i.id in (?0);&resultSet=id,theusername&param=[[1,2]]
GET http://localhost:8080/demoSpringMvc4/nativequery?query=UPDATE User Set username = 'ratata' where username = '?';&queryMode=U
 
POST http://localhost:8080/demoSpringMvc4/nativequery
{
	"data1":{
		"query":"Select * from User a where a.id =-1",
		"className":"com.ratata.model.User",
		"queryMode":"S"
	},
	"data2":{
		"query":"Select i.data,i.id from Item i where i.id = 1",
		"resultSet":["thedata","theid"],
		"queryMode":"S"
	},
	"data3":{
		"query":"Select * from User"
	},
	"data4":{
		"someData":4,
		"deepper":[
			{
				"query":"Select * from User",
				"className":"com.ratata.model.User",
				"mergeArray":true
			},[4,3],{
				"query":"Select * from Item",
				"className":"com.ratata.model.Item",
				"mergeArray":true
			}
		]
	}
}



POST http://localhost:8080/demoSpringMvc4/SaveQueryList
{
	"hello":{
		"query":"Select i.data,i.id from Item i where i.id in (?0,?1)",
		"resultSet":["thedata","theid"]
	},
	"hi":{
		"query":"Select i.data,i.id from Item i where i.id = 1",
		"resultSet":["thedata","theid"],
		"queryMode":"S"
	}
}

{
  "hi": {
    "data1": {
      "query": "Select * from User a where a.id =-1",
      "className": "com.ratata.model.User",
      "queryMode": "S"
    },
    "data2": {
      "query": "Select i.data,i.id from Item i where i.id = 1",
      "resultSet": [
        "thedata",
        "theid"
      ],
      "queryMode": "S"
    },
    "data3": {
      "query": "Select * from User"
    },
    "data4": {
      "someData": 4,
      "deepper": [
        {
          "query": "Select * from User",
          "className": "com.ratata.model.User",
          "mergeArray": true
        },
        [
          4,
          3
        ],
        {
          "query": "Select * from Item",
          "className": "com.ratata.model.Item",
          "mergeArray": true
        }
      ]
    }
  },
  "hello": {
    "query": "Select i.data,i.id from Item i where i.id in (?0)",
    "resultSet": [
      "thedata",
      "theid"
    ]
  }
}

http://localhost:8080/demoSpringMvc4/CustomQuery?queryName=hello&param=[[1,2]]

jointable
http://localhost:8080/demoSpringMvc4/nativequery?query=select b.id,b.data from User a join Item b on b.userId = a.id where a.id=3;&resultSet=id,data

save
POST http://localhost:8080/demoSpringMvc4/SaveObject?className=com.ratata.model.User
 {   "username": "hellno",
    "version": 0,
    "items": [
      {
        "data": "alan"
      },
      {
        "data": "alan"
      }
    ]
  }