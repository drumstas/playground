package main

import "fmt"

func main() {
	DeploymentOptions := [...]string{"R-pi", "AWS", "GCP", "Azure"}

	for index, option := range DeploymentOptions {
		fmt.Println(index, option)
	}
}
