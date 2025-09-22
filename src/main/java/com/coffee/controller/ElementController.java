package com.coffee.controller;

import com.coffee.entity.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController // 컨트롤러는 특정 요청에 대한 처리를 수행해 줍니다.
public class ElementController {
    @GetMapping("/element")
    public Element test(){
        Element bean = new Element() ;
        bean.setId(1);
        bean.setName("프렌치 바게트");
        bean.setPrice(1000);
        bean.setCategory("bread");
        bean.setStock(111);
        bean.setImage("french_baguette_01.png");
        bean.setDescription("프랑스의 대표적인 빵 중 하나로, 길쭉하고 얇은 형태의 식빵입니다. 바삭하면서도 촉촉한 식감과 진한 맛이 특징입니다.");
        return bean ;
    }

    @GetMapping("/element/list")
    public List<Element> test02(){
        List<Element> someList = new ArrayList<>();

        someList.add(new Element(1, "프렌치 바게트", 1000, "bread", 111, "french_baguette_01.png", "프랑스의 대표적인 빵 중 하나로, 길쭉하고 얇은 형태의 식빵입니다. 바삭하면서도 촉촉한 식감과 진한 맛이 특징입니다."));

        someList.add(new Element(2, "크로와상", 2000, "bread", 222, "croissant_02.png", "프랑스의 대표적인 베이커리 중 하나로, 층층이 쌓인 반죽에 버터를 추가하여 구워낸 과자입니다." ));

        someList.add(new Element(3, "아메리카노", 3000, "beverage", 12345, "americano01.png", "에스프레소의 쓴 맛과 향을 좋아하는 사람들이 물을 추가해서 즐기는 음료로, 물과 에스프레소의 비율에 따라서 쓴 맛과 진하게 즐길 수 있습니다."));

        someList.add(new Element(4, "카푸치노", 4000, "beverage", 444, "cappuccino01.png", "스팀밀크와 거품을 올린 것을 섞어 만든 이탈리아의 전통적인 커피 음료입니다."));

        return someList ;
    }
}
/*  },
        { id: 5, name: "스폰지 케이크", price: 5000, category: 'cake', stock: 555, image: 'sponge_cake_01.png', description: "가장 일반적인 케이크로, 부드럽고 공기가 많은 스폰지 텍스처를 가지고 있습니다. 일반적으로 크림, 과일, 초콜릿 등 다양한 토핑과 함께 제공됩니다." },
        { id: 6, name: "초콜릿 케이크", price: 6000, category: 'cake', stock: 666, image: 'chocolate_cake_01.png', description: "초콜릿으로 만든 케이크로, 풍부하고 진한 초콜릿 맛을 가지고 있습니다. 초콜릿으로 만든 케이크 스폰지와 초콜릿으로 만든 크림 또는 가나슈를 사용하여 제작됩니다." },
        { id: 7, name: "바닐라 마카롱", price: 2500, category: 'macaron', stock: 120, image: 'vanilla_macaron.png', description: "부드럽고 달콤한 바닐라 크림이 들어 있는 프랑스식 디저트입니다. 겉은 바삭하고 속은 촉촉한 식감이 특징입니다." },
        { id: 8, name: "딸기 마카롱", price: 2800, category: 'macaron', stock: 90, image: 'strawberry_macaron.png', description: "상큼한 딸기 크림이 가득 들어 있는 마카롱으로, 달콤하면서도 상큼한 맛을 즐길 수 있습니다." }
    ])*/