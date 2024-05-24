package extricount.tricountapi.service;

import extricount.tricountapi.MemberContext;
import extricount.tricountapi.model.BalanceResult;
import extricount.tricountapi.model.ExpenseResult;
import extricount.tricountapi.model.Member;
import extricount.tricountapi.model.Settlement;
import extricount.tricountapi.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;


    public Settlement createSettlement(String settlementName){
        Settlement settlement = settlementRepository.create(settlementName);
        settlementRepository.addParticipantToSettlement(settlement.getId(), MemberContext.getMember().getId());
        settlement.getParticipants().add(MemberContext.getMember());

        return settlement;

    }

    public void joinSettlement(Long settlementId) {
        Long memberId = MemberContext.getMember().getId();

        // 해당 멤버가 이미 정상방에 참여 중인지 체크
        boolean isParticipant = settlementRepository.existsParticipant(settlementId, memberId);

        if (!isParticipant) {
            settlementRepository.addParticipantToSettlement(settlementId, memberId);
        } else {
            throw new IllegalStateException("이미 해당 정상방에 참여 중입니다.");
        }
    }



    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    public void deleteSettlement(Long settlementId) {
        settlementRepository.deleteById(settlementId);
    }

    public List<BalanceResult> getBalanceResult(Long settlementId) {
        Map<Member, List<ExpenseResult>> collect = settlementRepository.findExpensesWithMemberBySettlementId(settlementId)
                .stream().collect(Collectors.groupingBy(ExpenseResult::getPayerMember));




        Map<Member, BigDecimal> memberAmountSumMap = collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, memberListEntry ->
                memberListEntry.getValue().stream().map(ExpenseResult::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));



        BigDecimal sumAmount = memberAmountSumMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add); // 4000
        BigDecimal averageAmount = sumAmount.divide(BigDecimal.valueOf(memberAmountSumMap.size()), BigDecimal.ROUND_DOWN); //2000



        Map<Member, BigDecimal> calcuratedAmountMap = memberAmountSumMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, memberBigDecimalEntry -> memberBigDecimalEntry.getValue().subtract(averageAmount)));

        List<Map.Entry<Member, BigDecimal>> receivers = calcuratedAmountMap.entrySet().stream()
                .filter(memberBigDecimalEntry -> memberBigDecimalEntry.getValue().signum() > 0)
                .collect(Collectors.toList());

        List<Map.Entry<Member, BigDecimal>> senders = calcuratedAmountMap.entrySet().stream()
                .filter(memberBigDecimalEntry -> memberBigDecimalEntry.getValue().signum() < 0)
                .collect(Collectors.toList());



        List<BalanceResult> balanceResults = new ArrayList<>();
        int receiverIndex = 0;
        int senderIndex = 0;
        while (receiverIndex < receivers.size() && senderIndex < senders.size()) {
            BigDecimal amountToTransfer = receivers.get(receiverIndex).getValue().add(senders.get(senderIndex).getValue());

            if(amountToTransfer.signum() < 0) {
                balanceResults.add(new BalanceResult(
                        senders.get(senderIndex).getKey().getId(),
                        senders.get(senderIndex).getKey().getName(),
                        receivers.get(receiverIndex).getValue().abs(),
                        receivers.get(receiverIndex).getKey().getId(),
                        receivers.get(receiverIndex).getKey().getName()
                ));
                receivers.get(receiverIndex).setValue(BigDecimal.ZERO);
                senders.get(senderIndex).setValue(amountToTransfer);
                receiverIndex++;
            } else if (amountToTransfer.signum() > 0) {
                balanceResults.add(new BalanceResult(
                        senders.get(senderIndex).getKey().getId(),
                        senders.get(senderIndex).getKey().getName(),
                        senders.get(senderIndex).getValue().abs(),
                        receivers.get(receiverIndex).getKey().getId(),
                        receivers.get(receiverIndex).getKey().getName()
                ));
                receivers.get(receiverIndex).setValue(amountToTransfer);
                senders.get(senderIndex).setValue(BigDecimal.ZERO);
                senderIndex++;
            } else {
                balanceResults.add(new BalanceResult(
                        senders.get(senderIndex).getKey().getId(),
                        senders.get(senderIndex).getKey().getName(),
                        senders.get(senderIndex).getValue().abs(),
                        receivers.get(receiverIndex).getKey().getId(),
                        receivers.get(receiverIndex).getKey().getName()
                ));
                receivers.get(receiverIndex).setValue(BigDecimal.ZERO);
                senders.get(senderIndex).setValue(BigDecimal.ZERO);
                receiverIndex++;
                senderIndex++;
            }
        }

        return balanceResults;

    }
}
