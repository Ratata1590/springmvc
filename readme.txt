GET http://localhost:8080/demoSpringMvc4/nativequery?query=select * from Item a;&className=com.ratata.model.Item


POST http://localhost:8080/demoSpringMvc4/nativequery
{
	"data1":{
		"query":"Select * from User a where a.id =-1",
		"className":"com.ratata.model.User",
		"singleReturn":true
	},
	"data2":{
		"query":"Select i.data,i.id from Item i where i.id = 1",
		"resultSet":["thedata","theid"],
		"singleReturn":true
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
			},[4,3],			{
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
		"singleReturn":true
	}
}

http://localhost:8080/demoSpringMvc4/CustomQuery?queryName=hello&param=1,2

