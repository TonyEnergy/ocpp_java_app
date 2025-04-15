package github.tonyenergy.controller;

import github.tonyenergy.entity.InventoryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
//@CrossOrigin(origins = "https://yourname.github.io")
@RestController
@RequestMapping("/api/items")
@Slf4j
public class InventoryController {

    private List<InventoryItem> inventory = new ArrayList<>();

    public InventoryController() {
        // Init mock data
        inventory.add(new InventoryItem("1", "ITM-001", "Solar Panel", 20, "https://tonyenergy.oss-cn-beijing.aliyuncs.com/static_pics/board.jpg"));
        inventory.add(new InventoryItem("2", "ITM-002", "Battery B500", 15, "https://tonyenergy.oss-cn-beijing.aliyuncs.com/static_pics/board.jpg"));
        inventory.add(new InventoryItem("3", "ITM-003", "Inverter", 10, "https://tonyenergy.oss-cn-beijing.aliyuncs.com/static_pics/board.jpg"));
        inventory.add(new InventoryItem("4", "ITM-004", "Wall Mount Kit", 50, "https://tonyenergy.oss-cn-beijing.aliyuncs.com/static_pics/board.jpg"));
    }

    @GetMapping
    public List<InventoryItem> getItems(@RequestParam(value = "name", required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            return inventory;
        }
        log.info("getItems");
        String lower = name.toLowerCase();
        return inventory.stream()
                .filter(item -> item.getItemName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
