package com.example.serenakd.roundup.service.Impl;

import com.example.serenakd.roundup.model.Amount;
import com.example.serenakd.roundup.model.SavingsGoals;
import com.example.serenakd.roundup.service.SavingsGoalService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.serenakd.roundup.util.Utilities.createHttpHeaders;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
@Service
@Slf4j
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final String bearerToken = "Bearer eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_31Uy47bMAz8lcLn1cLxO7711h_oB1AUlQixJUOSd7so-u-VLTmO06C3zAwfQ4rO70w5l_UZTIoJGs2782AHpS8c9O0dzZi9ZW7mIUI0WJ1lW7G6qytWFTWxri0KRsg7TjwvilMdgunXlPWnpi7LwFTdW6bARyLo3UIAopm1_2EGQfanEqE25QXPy1oEC5SzSlDDuMSSVZJj053kqcNzqO3NjXTMaGQpGimB5bI9s4rnLeMcW9YS5mXL26JqqpARxvqOSM7FrJqf87wVyLggyaqyW36dBON10VW8Efx8Wvo4NBMtS4lOGQ7Gkegtgfi2cdfVPtMw0kvBf01PghKkvZKK7JEflPMHJgEhbDDek1D-DqLiPeB1pHvkjj-t8vQNZn81VrnwjExpoT6UmGGIwRwG0JisIVjB0GhvzRAbLUzSjJbKjuCV0cxIJmct3F1y9-4biK1xdt6M24g0gkqFBwpG9KWHaRq-7miNGkEL8NQLGiiU2GDS7I38MshkSZKl4N39T4o2ojYNgBQ24Oli1zkeE_8VUypZvMI23UgeghvoMcBVTXgdaoIvok2KIA0RwR7E1AiXNFMiQmert2eJ8Q-yt6Ad4O460IzPw63fXpd2ancQ8W4i4q3AciPh3kbl95qDwWDiocJKMLMcyTObsqyRathGijMeqDXKEpKa_AG4oxQX7uAjPKJjF7P7OHBpmgO31nlk4sLCGbwqsYsvau1iLIpXEvNAgqWFJZq8DwPOU4ITbB9R-Mdcj5kZKx7aH9mt75F9kc_Mp77zntYXQ_fxTE1CJmrmDm3Y5nIoW5dHbo16vKb1wZ7PK_vzF_jBF_cKBgAA.k0mDkKChHVU_2wPhO4RJp7G7hedEJqyhLPu6i1LLEJaVAM6lOP1DP9a9R0QT7qiP4d0GwBRIfynwxDXOs_vjbch2aB59EJkB8bO1fQyFfu8KfrwNZDEfrwj-2HnWOCm9QJNcNn7MExHN1hM_r_886Gudsfo2yb0_vMD9POPRWFNb0BKwHGAtuuxD8-Bu-9PGu9s6PbM3V5cfn4hvatmotav8XUJPOqbrtOp_Ir9EmnNgw_HtfmiJF4Wd1kB6eexsBitLhxomwc5b95GWzadHVgce8QfgdybxC5UuHwR9I-L735Aj5GTpuFBV2mDRJP1sAAXcGI73OJZOCPnKEjGXAT1xudpq3jxsM-dWWNFnRx0SOvG4vz6O2yZXln80igi9nYmVhphYojLSw9RBjJl2TB90ZEfqyVRUBkSaVBPCO54hhP9yxtmZwinGm_UsdINfwEkrNz1C4i5UvfSiYnGOjCr4dQOgNtTDQZkHCt45fKNFV5K1rDeQu8LYSfrlJLofOy_P9Qp2nN9wj39H5M78qJVIL5D_sdhCVFmFiP26Jt4tvc3K8_wUolZ_i-43bADNyg32K3nanYQMgnaif8KeAhXhXteAYdcEL54m72q3XH-h4xwoUzjxwcWSqjyKywcW5NoZ33P7RQD6TZH5cZy7xB-XMSaSbc900-jujxPaa0w";

    private final RestTemplate restTemplate;

    private final AccountServiceImpl accountService;

    private final HttpHeaders httpHeaders = new HttpHeaders();

    @Autowired
    public SavingsGoalServiceImpl(RestTemplate restTemplate, AccountServiceImpl accountService) {
        this.restTemplate = restTemplate;
        this.accountService = accountService;
    }

    /**
     * @return
     */
    @Override
    public String createNewSavingsGoal() {
        String urlTemplate = String.format("https://api-sandbox.starlingbank.com/api/v2/account/%s/savings-goals", accountService.getAccountInfoAPI().accountUid());
        createHTTPHeadersWithBody();

        Amount target = new Amount();
        target.setCurrency("GBP");
        target.setMinorUnits(10000);
        SavingsGoalRequest body = new SavingsGoalRequest("Round-up Savings", "GBP", target);

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpEntity<SavingsGoalRequest> requestEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<SavingGoalResponse> response = restTemplate.exchange(urlTemplate, HttpMethod.PUT, requestEntity, SavingGoalResponse.class);

        return Objects.requireNonNull(response.getBody()).savingsGoalUid();
    }

    /**
     *
     */
    @Override

    public void addToSavingsGoal(String savingsGoalUid, int sweepingAmount) {
        final String transferUid = UUID.randomUUID().toString();
        String urlTemplate = String.format("https://api-sandbox.starlingbank.com/api/v2/account/%s/savings-goals/%s/add-money/%s", accountService.getAccountInfoAPI().accountUid(), savingsGoalUid,transferUid);
        createHTTPHeadersWithBody();

        Amount transfer = new Amount();
        transfer.setCurrency("GBP");
        transfer.setMinorUnits(sweepingAmount);
        SavingsGoalTransferRequest requestBody = new SavingsGoalTransferRequest(transfer);

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpEntity<SavingsGoalTransferRequest> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<SavingGoalTransferResponse> response = restTemplate.exchange(urlTemplate, HttpMethod.PUT, requestEntity, SavingGoalTransferResponse.class);

        Objects.requireNonNull(response.getStatusCode());
    }

    /**
     * @return
     */
    @Override
    public List<SavingsGoals> getAllSavingsGoals() {
        String urlTemplate = String.format("https://api-sandbox.starlingbank.com/api/v2/account/%s/savings-goals", accountService.getAccountInfoAPI().accountUid());
        ResponseEntity<SavingsGoalsResponse> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, createHttpHeaders(bearerToken, httpHeaders), SavingsGoalsResponse.class);
        log.info("Retrieving all savings goals");
        return Objects.requireNonNull(response.getBody()).savingsGoalList();

    }

    @Override
    public boolean isGoalAlreadyPresent(){
        List<SavingsGoals> savingsGoals = getAllSavingsGoals();
        for (SavingsGoals savingsGoal: savingsGoals) {
            String goalName = savingsGoal.name();
            if(goalName.equals("Round-up Savings")){
                log.info("'Round-up Savings goal' exists of {}", goalName);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRoundUpSavingsGoalUid(){
        List<SavingsGoals> savingsGoals = getAllSavingsGoals();
        for (SavingsGoals savingsGoal: savingsGoals) {
            String goalName = savingsGoal.name();
            if(goalName.equals("Round-up Savings")){
                log.info("Retrieving 'Round-up Savings' Uid");
                return savingsGoal.savingsGoalUid();
            }
        }
        return null;
    }

    public record SavingGoalResponse(String savingsGoalUid, Boolean success) {
    }

    public record SavingGoalTransferResponse(String transferUid, Boolean success) {

    }

    public record SavingsGoalsResponse(List<SavingsGoals> savingsGoalList) {

    }

    public record SavingsGoalRequest(String name, String currency, Amount target) {

    }

    private void createHTTPHeadersWithBody() {
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AUTHORIZATION, bearerToken);
    }

    @ToString
    @Getter
    @Setter
    public static class SavingsGoalTransferRequest {
        private Amount amount;

        public SavingsGoalTransferRequest(Amount amount) {
            this.amount = amount;
        }

    }
}
