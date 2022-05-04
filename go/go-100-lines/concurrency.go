package main

import "fmt"

func main() {
	limit := 5
	c := make(chan int)
	for i := 0; i < limit; i++ {
		go cookingGopher(i, c)
	}
	for i := 0; i < limit; i++ {
		gopherId := <-c
		fmt.Println("gopher", gopherId, "finished the dish")
	}
}

func cookingGopher(id int, c chan int) {
	fmt.Println("gopher", id, "started cooking")
	c <- id
}
