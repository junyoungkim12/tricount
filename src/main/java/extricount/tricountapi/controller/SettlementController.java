package extricount.tricountapi.controller;


import extricount.tricountapi.model.Settlement;
import extricount.tricountapi.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping("/settles/create")
    private ResponseEntity<Object> createSettlement(@RequestParam String settlementName){
    Settlement settlement = settlementService.createSettlement(settlementName);
    return new ResponseEntity<>(settlement, HttpStatus.OK);
    }

    @PostMapping("/settles/{id}/join")
    public ResponseEntity<Void> joinSettlement(@PathVariable("id") Long settlementId){
        settlementService.joinSettlement(settlementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/settles/{id}/balance")
    public ResponseEntity<Object> getSettlementBalanceResult(@PathVariable("id") Long settlementId) {
        return new ResponseEntity<>(settlementService.getBalanceResult(settlementId), HttpStatus.OK);
    }

    @GetMapping("/settles")
    public ResponseEntity<List<Settlement>> getAllSettlements() {
        List<Settlement> settlements = settlementService.getAllSettlements();
        return new ResponseEntity<>(settlements, HttpStatus.OK);
    }

    @DeleteMapping("/settles/{id}")
    public ResponseEntity<Void> deleteSettlement(@PathVariable("id") Long settlementId) {
        settlementService.deleteSettlement(settlementId);
        return ResponseEntity.noContent().build();
    }
}
