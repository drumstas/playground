package main

import "fmt"

func main() {
	var DeploymentOptions = [4]string{"R-pi", "AWS", "GCP", "Azure"}

	for i := 0; i < len(DeploymentOptions); i++ {
		option := DeploymentOptions[i]
		fmt.Println(i, option)
	}
}
