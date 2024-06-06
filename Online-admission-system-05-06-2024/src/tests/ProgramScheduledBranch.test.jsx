import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { vi, describe, it, expect } from 'vitest';
import ProgramScheduleBranch from '../components/ProgramScheduleBranch';
import { getProgramScheduleByBranch } from '../Services/ProgramScheduleService';

// Mock the service
vi.mock('../Services/ProgramScheduleService', () => ({
  getProgramScheduleByBranch: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ branchId: '123' }),
  };
});

describe('ProgramScheduleBranch', () => {
 

  it('renders no program schedule message when no schedules are found', async () => {
    getProgramScheduleByBranch.mockResolvedValueOnce({ data: [] });

    render(
      <BrowserRouter>
        <ProgramScheduleBranch />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('No Program Schedule Found')).toBeInTheDocument();
    });
  });

  it('navigates to apply now when "Apply Now" is clicked', async () => {
    getProgramScheduleByBranch.mockResolvedValueOnce({
      data: [
        {
          scheduledId: '1',
          programScheduledResponseDto: {
            branchName: 'Branch 1',
            name: 'University 1',
            collegeName: 'College 1',
            programName: 'Program 1',
            courseName: 'Course 1',
            eligibility: 'Eligibility 1',
            duration: '1 Year',
            degreeOffered: 'Degree 1',
          },
          startDate: '2024-01-01',
          endDate: '2024-12-31',
        },
      ],
    });

    render(
      <BrowserRouter>
        <ProgramScheduleBranch />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Apply Now')).toBeInTheDocument();
    });

    const applyNowButton = screen.getByText('Apply Now');
    fireEvent.click(applyNowButton);
    expect(mockNavigate).toHaveBeenCalledWith('/program-schedule/1');
  });
});
