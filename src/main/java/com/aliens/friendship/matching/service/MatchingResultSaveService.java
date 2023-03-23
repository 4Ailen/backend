package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.domain.Matching;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.repository.MatchingRepository;
import com.aliens.friendship.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.matching.repository.ApplicantRepository;
import com.aliens.friendship.matching.service.model.Participant;
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

    public void saveMatchingResult(List<Participant> participants, int numberOfMatches) {
        deleteAllMatchings();
        deleteAllChattingRooms();
        createChattingRooms(numberOfMatches);
        List<Matching> chattings = new ArrayList<>();

        for (Participant participant : participants) {

            Applicant applicant = applicantRepository.findById(participant.getId()).get();
            applicant.updateIsMatched(Applicant.Status.MATCHED);
            applicantRepository.save(applicant);

            List<com.aliens.friendship.matching.service.model.Matching> matchings = participant.getMatchingList();
            for (com.aliens.friendship.matching.service.model.Matching matching : matchings) {
                ChattingRoom chattingRoom = chattingRoomRepository.findById(matching.getChattingRoomId()).get();
                Applicant partner = applicantRepository.findById(matching.getPartner().getId()).get();
                Matching chatting = Matching.builder()
                        .chattingRoom(chattingRoom)
                        .applicant(partner)
                        .build();
                chattings.add(chatting);
            }
        }

        matchingRepository.saveAll(chattings);
    }

    private void createChattingRooms(int numberOfChattingRooms){
        for(int i = 0; i < numberOfChattingRooms; i++){
            ChattingRoom chattingRoom = ChattingRoom.builder()
                    .id(i)
                    .status(ChattingRoom.RoomStatus.CLOSE)
                    .build();
            chattingRoomRepository.save(chattingRoom);
        }
    }

    private void deleteAllChattingRooms(){
        chattingRoomRepository.deleteAll();
    }

    private void deleteAllMatchings(){
        matchingRepository.deleteAll();
    }
}
