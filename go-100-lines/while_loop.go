package main

import "fmt"

func main() {
	count := 1
	for count < 5 {
		count += count
	}
	fmt.Println(count)
}
