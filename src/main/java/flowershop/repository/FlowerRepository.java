package flowershop.repository;

import flowershop.entity.Flower;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class FlowerRepository {

  private final List<Flower> flowers = new ArrayList<>();

  public FlowerRepository() {
    flowers.add(new Flower(1, "Роза", 4, "белый", "Нидерланды"));
    flowers.add(new Flower(2, "Тюльпан", 3, "розовый", "Эквадор"));
    flowers.add(new Flower(3, "Хризантема", 3, "розовый", "Испания"));
    flowers.add(new Flower(4, "Пион", 9, "красный", "Испания"));
    flowers.add(new Flower(5, "Гвоздика", 7, "красный", "Нидерланды"));

  }

  public List<Flower> findAll() {
    return flowers;
  }

}
