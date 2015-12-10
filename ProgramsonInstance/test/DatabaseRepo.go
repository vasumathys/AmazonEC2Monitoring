package main

import (
	"log"
	"gopkg.in/mgo.v2"
	"time"
)

var todos Locations
var itr int

type Ctr struct {
  Ctr   int
  Time 	string
  Instance string
}

// Fetch all the results for the handler 'Get/'
func RepoFindAll() []Location {
	session, err := mgo.Dial("mongodb://bhavana:bhavana@ds037244.mongolab.com:37244/tests")
	c := session.DB("tests").C("test")
	result := []Location{}
	iterator := []Ctr{}
	err = c.Find(nil).All(&result)
	if err != nil {
		log.Fatal(err)
	}
	
	s := session.DB("tests").C("ctr")
	err = s.Find(nil).All(&iterator)
    if err != nil {
		log.Fatal(err)
	}	
    ctr := len(iterator)
	if ctr != 0 {
	 itr =  ctr+ 1
	} else {
	 itr = itr + 1
	}

	tim := time.Now().UTC()
    err = s.Insert(&Ctr{Ctr : itr,Time : tim.String(),Instance : "i-094c05bb"})
	if err != nil {
		log.Fatal(err)
	}
	 
	return result
}

// Handler for the POST
func RepoCreateTodo(t Location) Location {
	session, err := mgo.Dial("mongodb://bhavana:bhavana@ds037244.mongolab.com:37244/tests")

	//todos = append(todos, t)
	c := session.DB("tests").C("test")
	result := []Location{}
	iterator := []Ctr{}
	err = c.Find(nil).All(&result)
	if len(result) == 0 {
		t.Id = 12345
	} else {
		t.Id = 12345 + len(result)
	}
	err = c.Insert(&Location{Id: t.Id, Name: t.Name})
	if err != nil {
		log.Fatal(err)
	}
	itr = itr + 1
	s := session.DB("tests").C("ctr")	
	err = s.Find(nil).All(&iterator)
    if err != nil {
		log.Fatal(err)
	}	
	ctr := len(iterator)
	if ctr != 0 {
	 itr =  ctr+ 1
	} else {
	 itr = itr + 1
	}

    tim := time.Now().UTC()
    err = s.Insert(&Ctr{Ctr : itr,Time : tim.String(),Instance : "i-094c05bb"})
	if err != nil {
		log.Fatal(err)
	}
	return t
}



