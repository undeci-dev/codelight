export interface PollOptionCreateRequest {
  optionText: string;
  displayOrder?: number;
}

export interface PollCreateRequest {
  question: string;
  options: PollOptionCreateRequest[];
  multipleChoice?: boolean;
  endsAt?: string;
}

export interface PollVoteRequest {
  optionIds: number[];
}

export interface PollUpdateRequest {
  question?: string;
  options?: PollOptionCreateRequest[];
  multipleChoice?: boolean;
  endsAt?: string;
}

export interface PollOptionResponse {
  optionId: number;
  optionText: string;
  votesCount: number;
  displayOrder: number;
  percentage: number;
}

export interface PollResponse {
  pollId: number;
  question: string;
  multipleChoice: boolean;
  endsAt?: string;
  totalVotes: number;
  hasVoted: boolean;
  isExpired: boolean;
  votedOptionIds: number[];
  options: PollOptionResponse[];
}
