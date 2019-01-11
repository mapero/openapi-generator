/*
 * OpenAPI Petstore
 *
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 *
 * API version: 1.0.0
 * Generated by: OpenAPI Generator (https://openapi-generator.tech)
 */

package petstore

type Dog struct {
	ClassName string `json:"className" xml:"className"`
	Color string `json:"color,omitempty" xml:"color"`
	Breed string `json:"breed,omitempty" xml:"breed"`
}
