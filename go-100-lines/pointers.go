package main

import "fmt"

func main() {
	var address *int
	number := 42
	address = &number
	value := *address

	fmt.Printf("address: %v\n", address)
	fmt.Printf("value: %v\n", value)
}
