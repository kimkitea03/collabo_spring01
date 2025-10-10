package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Value("${productImageLocation}")
    private String productImageLocation ; //기본값 : null;

    @Autowired
    private ProductService productService ;

//    @GetMapping("/list") //상품 목록을 List 컬렌션으로 반환해 줍니다.
//    public List<Product> list(){
//        List<Product> products = this.productService.getProductList() ;
//
//        return products ;
//    }


    @GetMapping("/list") // 페이징 관련 파라미터를 사용하여 상품 목록을 조회합니다.
    public ResponseEntity<Page<Product>> listProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize
    ){
        System.out.println("pageNumber : " + pageNumber + ", pageSize : "+pageSize);

        // 상품 번호가 큰 것 부터 정렬합니다.
        Sort mysort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, mysort);

//        현재 페이지는 pageNumber이고 페이지당 보여줄 갯수는 pageSize를 사용하여 Pageble 페이지를 구합니다.
        Page<Product> productPage = productService.listProducts(pageable) ;

        return ResponseEntity.ok(productPage);
    }

    //클라이언트가 특정 상품 id에 대하여 "삭제" 요청을 하였습니다.
    //@PathVariable는 URL의 경로 변수를 메소드의 매개 변수로 전달해 줍니다.
    @DeleteMapping("/delete/{id}") // {id}는 경로 변수라고 하며, 가변 매개변수로 이해하면 됩니다.
    public ResponseEntity<String> delete(@PathVariable Long id){ // {id}으로 넘겨온 상품의 아이디가, 변수 id에 할당됩니다.
        try{
        boolean isDeleted = this.productService.deleteProduct(id);

        if (isDeleted){
            return ResponseEntity.ok(id+"상품이 삭제되었습니다.");
        }else {
            return ResponseEntity.badRequest().body(id+"번 상품이 존재하지 않습니다.");
        }

        } catch (Exception err) {
            return ResponseEntity.internalServerError().body("오류 발생 : "+err.getMessage());
        }
    }

    //상품 등록하기
    @PostMapping("/insert")
    public  ResponseEntity<?> insert(@RequestBody Product product){
        //@RequestBody : http를 사용하여 넘어온 데이터(body)를 자바 객체 형식으로 변환해 줍니다.
        
        //데이터 베이스와 이미지 경로에 저장될 이미지 이름
        String imageFileName = "product_"+System.currentTimeMillis()+".jpg";

        //String 클래스 공부 : endsWith(), split() 메소드

        //폴더 구분자가 제대로 설정되어 있으면 그대로 사용합니다.
        // 그렇지 않으면, 폴더 구분자를 붙여 줍니다.
        // File.separator : 폴더 구분자를 의미하며, 리눅스 /, 윈도우는 \입니다.
        String pathName = productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;

        File imageFile = new File(pathName + imageFileName);

        String imageData = product.getImage(); // Base64 인코딩 문자열 (엄청 긴)

        try{
            // 파일 정보를 byte 단위로 변환하여 이미지를 복사합니다.
            FileOutputStream fos = new FileOutputStream(imageFile);

            // 메소드 체이닝 :  점을 연속적으로 찍어서 메소드를 계속 호출하는 것
            byte[] decodedImage = Base64.getDecoder().decode(imageData.split(",")[1]);
            fos.write(decodedImage); //바이트 파일을 해당 이미지 경로에 복사하기

            product.setImage(imageFileName);
            product.setInputdate(LocalDate.now());

            this.productService.save(product);

            return ResponseEntity.ok(Map.of("message","Product insert successfully","image",imageFileName));

        } catch (Exception err) {
            return ResponseEntity.status(500).body(Map.of("message",err.getMessage(),"error","Error file uploading")) ;
        }
    }

    //프론트 앤드의 상품 수정 페이지에서 요청이 들어 왔습니다.
    @GetMapping("/update/{id}") //상품의 아이디 정보를 이용하여 해당 상품 Bean 객체를 반환해 줍니다.
    public ResponseEntity<Product> getUpdate(@PathVariable Long id){
        System.out.println("수정할 상품 번호 : "+id);

        Product product = this.productService.getProductById(id);

        if(product == null){//상품이 없으면 404 응답과 함께 null을 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        }else{// 해당 상품의 정보와 함께, 성공(200) 메시지를 반환
            return ResponseEntity.ok(product);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> putUpdate(@PathVariable Long id, @RequestBody Product updateProduct){
//        System.out.println("수정할 상품 id : " + id);
//
//        System.out.println("관리자가 수정한 상품 정보");
//        System.out.println(updateProduct);

        Optional<Product> findProduct = productService.findById(id);

        if (findProduct.isEmpty()){ // 상품이 존재하지 않으면 404 응답 반환
            return ResponseEntity.notFound().build();
        }else { // 상품이 존재합니다.
            // Optional에서 실제 상품 정보 끄집어 내기
            Product savedProduct = findProduct.get();

            try {
                // 이전 이미지 객체에 새로운 이미지 객체 정보를 업데이트합니다.
                savedProduct.setName(updateProduct.getName());
                savedProduct.setPrice(updateProduct.getPrice());
                savedProduct.setCategory(updateProduct.getCategory());
                savedProduct.setStock(updateProduct.getStock());
                savedProduct.setDescription(updateProduct.getDescription());
//                saveProduct.setInputdate(LocalDate.now());
                
                // 이미지가 의미있는 문자열로 되어 있고, Base64 인코딩 형식이면 이미지 이름을 변경
                if (updateProduct.getImage() != null && updateProduct.getImage().startsWith("data:image")){
                    String imageFileName = saveProductImage(updateProduct.getImage());
                    savedProduct.setImage(imageFileName);
                }

                //서비스를 통하여 데이터 베이스에 저장합니다
                this.productService.save(savedProduct);

                return ResponseEntity.ok(Map.of("massage","상품 수정 성공"));

            } catch (Exception err) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message",err.getMessage(),"error","Error product update failed"));
            }
        }
    }

    //Base64 인코딩 문자열을 변환하여 이미지로 만들고, 저장해주는 메소드입니다.
    private String saveProductImage(String base64Image) {

        String imageFileName = "product_"+System.currentTimeMillis()+".jpg";
        //폴더 구분자가 제대로 설정되어 있으면 그대로 사용합니다.
        // 그렇지 않으면, 폴더 구분자를 붙여 줍니다.
        // File.separator : 폴더 구분자를 의미하며, 리눅스 /, 윈도우는 \입니다.
        String pathName = productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;

        File imageFile = new File(pathName + imageFileName);

        //Base64Image : javaScript FileReader API에 만들어진 이미지입니다.
        byte[] decodedImage = Base64.getDecoder().decode(base64Image.split(",")[1]);

        try{ // FileOutputStream는 바이트 파일을 처리해주는 자바의 Stream 클래스
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(decodedImage);
            return imageFileName;

        } catch (Exception err) {
            err.printStackTrace();
            return base64Image ;
        }
    }

    // 이미지 경로를 반환하는 메서드
    private String getProductImagePath() {
        // 폴더 구분자가 제대로 설정 되어 있으면 그대로 사용합니다.
        // 그렇지 않으면, 폴더 구분자를 붙여 줍니다.
        // File.separator : 폴더 구분자를 의미하며, 리눅스는 /, 윈도우는 \입니다.
        return productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;
    }

    @GetMapping("/detail/{id}") // 프론트 엔드가 상품에 대한 상세 정보를 요청하였습니다.
    public ResponseEntity<Product> detail(@PathVariable Long id){
        Product product = this.productService.getProductById(id);

        if (product == null){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            return ResponseEntity.ok(product);
        }
    }

    @GetMapping("") // 홈페이지에 보여줄 큰 이미지들에 대한 정보를 읽어 옵니다.
    public List<Product> getBigsizeProduct(@RequestParam(required = false) String filter){
//        return ProductService.getProductByFilter(filter);
        return productService.getProductByFilter(filter);
    }

}
