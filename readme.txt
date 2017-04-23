GET http://localhost:8080/demoSpringMvc4/nativequery?query=select * from Item a;&className=com.ratata.model.Item

GET http://localhost:8080/demoSpringMvc4/nativequery?query=Select i.data,i.id from Item i where i.id in (?0);&resultSet=id,theusername&param=[[1,2]]
POST http://localhost:8080/demoSpringMvc4/nativequery
{
	"param":[[1,2]],
	"data":{
		"query":"Select i.data,i.id from Item i where i.id in (?0)",
		"resultSet":["thedata","theid"]
	}
}

GET http://localhost:8080/demoSpringMvc4/nativequery?query=UPDATE User Set username = 'ratata' where username = '?';&queryMode=U
 
POST http://localhost:8080/demoSpringMvc4/nativequery
{
	"param": [
		[]
	],
	"data": {
		"data1": {
			"query": "Select * from User a where a.id =1",
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
      "query": "Select * from User a where a.id =1",
      "className": "com.ratata.model.User",
      "queryMode": "S"
    },
    "data2": {
		"query":"Select i.data,i.id from Item i where i.id in (?0)",
		"resultSet":["thedata","theid"]
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
GET http://localhost:8080/demoSpringMvc4/CustomQuery?queryName=hi&param=[[],[[1,2]],[],[],[]]


{
  "hi": {
    "data1": {
      "query": "Select * from User a where a.id =1",
      "className": "com.ratata.model.User",
      "queryMode": "S"
    },
    "data2": {
		"linkquery":"hello"
	},
    "data3": {
      "query": "Select * from User"
    },
    "data4": {
      "someData": 4,
      "deepper": [
        {
		  "linkquery":"hello",
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
http://localhost:8080/demoSpringMvc4/CustomQuery?queryName=hi&param=[[],[[1,2]],[],[[1,2]],[]]

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
------------idea for custom fetching
if have insideObject select must have resultSet
insideObject
passParam
resultSet
query
queryMode S,L

http://localhost:8080/demoSpringMvc4/nativequery
{
	"param": [
		[]
	],
	"data": {
		"data1": {
			"query": "Select * from User a where a.id =1",
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
			"query": "Select a.id,a.data,a.userId from Item a",
			"resultSet": [
				"theid",
				"theusername",
				"userId"
			],
			"insideObject": {
				"userList": {
					"param": [
						"userId"
					],
					"data": {
						"query": "select a.id,a.username from User a where a.id!=?0",
						"resultSet": [
							"id",
							"username"
						]
					}
				}
			}
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
	}
}

http://localhost:8080/demoSpringMvc4/SaveNestedObject
update linked Object
{
	"id":3,
	"className":"com.ratata.model.User",
	"childList":[
			{
				"idList":"0,1",
				"className":"com.ratata.model.Item",
				"parentKey":"user"
			}
	]
}

// todo list:
crud bunk mode

multithread

[x]lock feature list

validate query
