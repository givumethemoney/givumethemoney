package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.NicknameDonation;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.repository.DonationRepository;
import com.hey.givumethemoney.repository.NicknameDonationRepository;
import com.hey.givumethemoney.repository.WaitingDonationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final WaitingDonationRepository waitingDonationRepository;
    private final NicknameDonationRepository nicknameDonationRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository, 
            WaitingDonationRepository waitingDonationRepository,
            NicknameDonationRepository nicknameDonationRepository) {
        this.donationRepository = donationRepository;
        this.waitingDonationRepository = waitingDonationRepository;
        this.nicknameDonationRepository = nicknameDonationRepository;
    }

    public WaitingDonation saveWaitingDonation(WaitingDonation wadingDonation) {
        return waitingDonationRepository.save(wadingDonation);
    }

    @Transactional
    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<WaitingDonation> getWaitingDonations() {
        return waitingDonationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Donation> getDonations() {
        return donationRepository.findAll();
    }

    public List<Donation> getOngoingDonations() {
        LocalDate today = LocalDate.now();
        return getDonations().stream()
                .filter(donation -> donation.getEndDate() != null && donation.getEndDate().isAfter(today))
                .collect(Collectors.toList());
    }

    public List<Donation> getFinishedDonations() {
        LocalDate today = LocalDate.now();
        return getDonations().stream()
                .filter(donation -> donation.getEndDate() != null && !donation.getEndDate().isAfter(today))
                .collect(Collectors.toList());
    }

    public Optional<WaitingDonation> getWaitingDonationById(Long id) {
        return waitingDonationRepository.findById(id);
    }
    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    public List<WaitingDonation> getWaitingDonationsByUserId(String userId) {
        List<WaitingDonation> donations = getWaitingDonations();
        List<WaitingDonation> result = new ArrayList<>();

        for (WaitingDonation d : donations) {
            if (d.getUserId().equals(userId)) {
                result.add(d);
            }
        }

        return result;
    }

    public List<Donation> getDonationsByUserId(String userId) {
        List<Donation> donations = getDonations();
        List<Donation> result = new ArrayList<>();

        for (Donation d : donations) {
            if (d.getUserId().equals(userId)) {
                result.add(d);
            }
        }

        return result;
    }

    public void deleteWaitingDonationById(Long id) {
        waitingDonationRepository.deleteById(id);
    }

    // 핵심 이름 추출 (단순화된 예제)
    private String extractCoreName(String nickname) {
        return nickname.replaceAll("[^가-힣a-zA-Z0-9]", "")  // 특수문자 제거
                       .toLowerCase();  // 대소문자 통일
    }

    // 유사 닉네임 확인 (Levenshtein 거리 활용)
    private boolean isSimilar(String name1, String name2) {
        int distance = levenshteinDistance(name1, name2);
        double similarity = 1 - (double) distance / Math.max(name1.length(), name2.length());
        return similarity >= 0.60;  // 유사도가 80% 이상일 때 같은 그룹으로 간주
    }

    // Levenshtein 거리 계산
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    // 닉네임 기부 추가
    public void addNicknameDonation(Long donationId, String nickname, int amount) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("기부를 찾을 수 없습니다."));

        String coreName = extractCoreName(nickname);

        // 기존 닉네임과 비교
        List<NicknameDonation> existingDonations = nicknameDonationRepository.findByDonationIdAndStatusTrue(donationId);
        String matchingKey = existingDonations.stream()
                                              .map(NicknameDonation::getNickname)
                                              .filter(existingNickname -> isSimilar(existingNickname, coreName))
                                              .findFirst()
                                              .orElse(coreName);

        // 닉네임 기부 저장
        NicknameDonation nicknameDonation = new NicknameDonation();
        nicknameDonation.setDonation(donation);
        nicknameDonation.setNickname(matchingKey); // 정규화된 닉네임 사용
        nicknameDonation.setAmount(amount);

        nicknameDonationRepository.save(nicknameDonation);
    }


    // 특정 Donation의 상위 30 닉네임 목록 조회
    public List<Map.Entry<String, Integer>> getTopNicknameDonations(Long donationId) {
        List<NicknameDonation> donations = nicknameDonationRepository.findByDonationId(donationId);

        // 닉네임별 금액 합산
        Map<String, Integer> nicknameTotals = new HashMap<>();
        for (NicknameDonation donation : donations) {
            String coreName = extractCoreName(donation.getNickname());

            // 기존 닉네임들과 유사한 그룹을 찾기
            String matchingKey = coreName;
            for (String existingNickname : nicknameTotals.keySet()) {
                if (isSimilar(existingNickname, coreName)) {
                    matchingKey = existingNickname;
                    break;
                }
            }

            // 유사한 닉네임끼리 금액을 합산
            nicknameTotals.put(matchingKey, nicknameTotals.getOrDefault(matchingKey, 0) + donation.getAmount());
        }

        // 금액 기준 정렬
        return nicknameTotals.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(30)
                .collect(Collectors.toList());
    }


}
