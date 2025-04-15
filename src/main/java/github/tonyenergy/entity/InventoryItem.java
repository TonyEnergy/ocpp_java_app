package github.tonyenergy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {
    private String id;
    private String itemCode;
    private String itemName;
    private int quantity;
    private String photoUrl;
}

