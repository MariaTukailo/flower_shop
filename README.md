# flower_shop
Spring Boot Flower Shop Project
This application is a flower shop management system based on the Spring Boot framework. It allows you to keep records of goods (colors), manage their characteristics, and provide access to data through a programming interface (API).

Laboratory № 1 : Basic REST service
1. A Spring Boot application has been created.
2. Implemented the Flower domain entity and the REST API for accessing the product catalog.
3. Flexible search mechanisms are implemented in the catalog:
   - Via @PathVariable: it is implemented to obtain detailed information about a specific flower by its catalog number.
   - Via @RequestParam: implemented assortment filtering by color, which allows you to find all items of the same color.
4.  Implemented layers: Controller → Service → Repository.
5.  Implemented DTO and mapper between Entity and API response.
