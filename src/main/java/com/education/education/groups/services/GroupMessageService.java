package com.education.education.groups.services;

import com.education.education.groups.DTO.request.SendGroupMessageRequest;
import com.education.education.groups.DTO.response.GroupMessageResponse;
import com.education.education.groups.DTO.response.PagedMessageResponse;
import com.education.education.groups.entities.Group;
import com.education.education.groups.entities.GroupMessage;
import com.education.education.groups.repositories.GroupMessageRepository;
import com.education.education.groups.repositories.GroupRepository;
import com.education.education.user.user.entities.User;
import com.education.education.user.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMessageService {

        private final GroupMessageRepository groupMessageRepository;
        private final GroupRepository groupRepository;
        private final UserRepository userRepository;
        private final SimpMessagingTemplate messagingTemplate;

        @Transactional
        public GroupMessageResponse sendMessage(UUID groupId, UUID senderId, SendGroupMessageRequest request) {
                Group group = groupRepository.findById(groupId)
                                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Verify sender is an active member of this group
                boolean isApprovedMember = group.getMembers().stream()
                                .anyMatch(member -> member.getUser().getId().equals(senderId));

                if (!isApprovedMember) {
                        throw new AccessDeniedException("You are not an approved member of this group");
                }

                GroupMessage message = GroupMessage.builder()
                                .group(group)
                                .sender(sender)
                                .content(request.content())
                                .build();

                GroupMessage savedMessage = groupMessageRepository.save(message);

                GroupMessageResponse response = new GroupMessageResponse(
                                savedMessage.getId(),
                                group.getId(),
                                sender.getId(),
                                sender.getFirstName(),
                                sender.getLastName(),
                                savedMessage.getContent(),
                                savedMessage.getSentAt());

                // Broadcast to WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/groups/" + groupId, response);

                return response;
        }

        public List<GroupMessageResponse> getGroupChatHistory(UUID groupId, UUID userId) {
                Group group = groupRepository.findById(groupId)
                                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

                boolean isMember = group.getMembers().stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId));

                if (!isMember) {
                        throw new AccessDeniedException(
                                        "You cannot view the chat history of a group you do not belong to");
                }

                List<GroupMessage> messages = groupMessageRepository.findByGroupIdOrderBySentAtAsc(groupId);

                return messages.stream()
                                .map(msg -> new GroupMessageResponse(
                                                msg.getId(),
                                                msg.getGroup().getId(),
                                                msg.getSender().getId(),
                                                msg.getSender().getFirstName(),
                                                msg.getSender().getLastName(),
                                                msg.getContent(),
                                                msg.getSentAt()))
                                .collect(Collectors.toList());
        }

        public PagedMessageResponse getPagedGroupChatHistory(UUID groupId, UUID userId, int page, int size) {
                Group group = groupRepository.findById(groupId)
                                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

                boolean isMember = group.getMembers().stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId));

                if (!isMember) {
                        throw new AccessDeniedException(
                                        "You cannot view the chat history of a group you do not belong to");
                }

                Page<GroupMessage> messagePage = groupMessageRepository.findByGroupId(
                                groupId,
                                PageRequest.of(page, size, Sort.by("sentAt").descending()));

                List<GroupMessageResponse> messageResponses = messagePage.getContent().stream()
                                .map(msg -> new GroupMessageResponse(
                                                msg.getId(),
                                                msg.getGroup().getId(),
                                                msg.getSender().getId(),
                                                msg.getSender().getFirstName(),
                                                msg.getSender().getLastName(),
                                                msg.getContent(),
                                                msg.getSentAt()))
                                .collect(Collectors.toList());

                return new PagedMessageResponse(
                                messageResponses,
                                messagePage.getNumber(),
                                messagePage.getTotalPages(),
                                messagePage.getTotalElements(),
                                messagePage.hasNext());
        }
}
