package extricount.tricountapi.service;

import extricount.tricountapi.model.Expense;
import extricount.tricountapi.model.Settlement;
import extricount.tricountapi.repository.ExpenseRepository;
import extricount.tricountapi.repository.SettlementRepository;
import extricount.tricountapi.request.ExpenseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final SettlementRepository settlementRepository;
    private final ExpenseRepository expenseRepository;

    public void addExpense(ExpenseRequest request) {
        Optional<Settlement> settlementOptional = settlementRepository.findById(request.getSettlementId());
        if(!settlementOptional.isPresent()) {
            throw new RuntimeException("존재하지 않는 정산방입니다.");
        }

        Expense expense = new Expense();
        expense.setName(request.getName());
        expense.setSettlementId(request.getSettlementId());
        expense.setPayerMemberId(request.getPayerMemberId());
        expense.setAmount(request.getAmount());
        expense.setExpenseDateTime(LocalDateTime.now());
        expenseRepository.save(expense);
    }

    public List<Expense> getAllExpensesBySettlementId(Long settlementId) {
        return expenseRepository.findAllBySettlementId(settlementId);
    }

    public void deleteExpense(Long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

}
