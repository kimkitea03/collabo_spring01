package com.coffee.controller;

import com.coffee.dto.OrderItemDto;
import com.coffee.dto.OrderResponseDto;
import com.coffee.dto.orderDto;
import com.coffee.entity.Member;
import com.coffee.entity.Order;
import com.coffee.entity.OrderProduct;
import com.coffee.entity.Product;
import com.coffee.service.CartProductService;
import com.coffee.service.MemberService;
import com.coffee.service.OrderService;
import com.coffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService ;
    private final MemberService memberService ;
    private final ProductService productService;
    private final CartProductService cartProductService;

    // 리액트의 '카트 목록' 이나 '주문하기' 버튼을 눌러서 주문을 시도합니다.
    @PostMapping("") // CartList.js 파일의 makeOrder() 함수와 연관이 있습니다.
    public ResponseEntity<?> order(@RequestBody orderDto dto){
        System.out.println(dto);

        // 회원(member) 객체 생성
        Optional<Member> optionalMember = memberService.findMemberById(dto.getMemberId());
        if(!optionalMember.isPresent()){
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }
        Member member = optionalMember.get() ;

        // 주문(product) 객체 생성
        Order order = new Order();
        order.setMember(member); // 이 사람이 주문자입니다.
        order.setOrderdate(LocalDate.now());//주문시점
        order.setStatus(dto.getStatus());

        // 주문 상품 (OrderPrducts)들은 확장 for 구문을 사용합니다.
        List<OrderProduct> orderProductList = new ArrayList<>();

        for(OrderItemDto item : dto.getOrderItems()){
            //item은 주문 하고자 하는 '주문 상품' 1개를 의미합니다.
            Optional<Product> optionalProduct = productService.findProductById(item.getProductId());

            if(!optionalProduct.isPresent()){
                throw new RuntimeException("해당 상품이 존재하지 않습니다.");
            }
            Product product = optionalProduct.get();

            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException("재고 수량이 부족합니다.");
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            orderProductList.add(orderProduct);

            // 상품의 재고 수량 빼기
            product.setStock(product.getStock() - item.getQuantity());

            // 카트에 담겨 있던 품목을 삭제해 줘야 합니다.
            Long cartProductId = item.getCartProductId() ;
            //System.out.println("cartProductId : " + cartProductId);

            if(cartProductId != null){ // 장바구니 내역에서 `주문하기` 버튼을 클릭한 경우에 해당함
                cartProductService.deleteCartProductById(cartProductId);

            }else{
                System.out.println("상품 상세 보기에서 클릭하셨군요.");
            }

        }

        order.setOrderProducts(orderProductList);

        // 주문 객체를 저장합니다.
        orderService.saveOrder(order);

        String message = "주문이 완료 되었습니다.";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/list/{memberId}")
    public ResponseEntity<List<OrderResponseDto>> getOrderList(@PathVariable Long memberId){
        Optional<Member> optionalMember = this.memberService.findMemberById(memberId);
        if (optionalMember.isEmpty()) {// 무효한 회원 정보
            return ResponseEntity.badRequest().build();
        }
        Member member = optionalMember.get();



        return null;
    }
}
