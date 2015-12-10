package main

type Location struct {
    Id          int `json:"id,omitempty"`
	Name        string `json:"name,omitempty"`
}

type Locations []Location