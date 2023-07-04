package com.aliens.friendship.domain.matching.service;

import com.aliens.friendship.domain.matching.service.model.ServiceModelMatching;
import com.aliens.friendship.domain.matching.service.model.Participant;
import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import com.aliens.friendship.domain.matching.repository.MatchingRepository;
import com.aliens.friendship.domain.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingResultSaveService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final MatchingRepository matchingRepository;
    private final ApplicantRepository applicantRepository;

    public void saveMatchingResult(List<Participant> participants, Long numberOfMatches) {
        deleteAllMatchings();
        deleteAllChattingRooms();
        createChattingRooms(numberOfMatches);
        List<com.aliens.friendship.domain.matching.domain.Matching> chattings = new ArrayList<>();

        for (Participant participant : participants) {

            Applicant applicant = applicantRepository.findById(participant.getId()).get();
            applicant.updateIsMatched(Applicant.Status.MATCHED);
            applicantRepository.save(applicant);

            List<ServiceModelMatching> serviceModelMatchings = participant.getServiceModelMatchingList();
            for (ServiceModelMatching serviceModelMatching : serviceModelMatchings) {
                ChattingRoom chattingRoom = chattingRoomRepository.findById(serviceModelMatching.getChattingRoomId()).get();
                Applicant partner = applicantRepository.findById(serviceModelMatching.getPartner().getId()).get();
                com.aliens.friendship.domain.matching.domain.Matching chatting = com.aliens.friendship.domain.matching.domain.Matching.builder()
                        .chattingRoom(chattingRoom)
                        .applicant(partner)
                        .build();
                chattings.add(chatting);
            }
        }

        matchingRepository.saveAll(chattings);
    }

    private void createChattingRooms(Long numberOfChattingRooms){
        for(int i = 0; i < numberOfChattingRooms; i++){
            ChattingRoom chattingRoom = ChattingRoom.builder()
                    .id(Long.valueOf(i))
                    .status(ChattingRoom.RoomStatus.CLOSE)
                    .build();
            chattingRoomRepository.save(chattingRoom);
        }
    }

    private void deleteAllChattingRooms(){
        chattingRoomRepository.deleteAll();
    }

    private void deleteAllMatchings(){
        matchingRepository.deleteAllInBatch();
    }
}
