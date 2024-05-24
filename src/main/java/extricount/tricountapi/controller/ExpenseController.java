package extricount.tricountapi.controller;



import extricount.tricountapi.model.Expense;
import extricount.tricountapi.request.ExpenseRequest;
import extricount.tricountapi.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/expenses/add")
    public ResponseEntity<Void> addExpenseToSettlement(@RequestBody ExpenseRequest expenseRequest) {
        expenseService.addExpense(expenseRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/settles/{id}/expenses")
    public ResponseEntity<List<Expense>> getAllExpensesBySettlementId(@PathVariable("id") Long settlementId) {
        List<Expense> expenses = expenseService.getAllExpensesBySettlementId(settlementId);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable("id") Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }



}
